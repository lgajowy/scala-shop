package lgajowy.shop.programs

import cats.MonadThrow
import cats.effect.kernel.Temporal
import cats.implicits._
import lgajowy.shop.clients.PaymentClient
import lgajowy.shop.domain.auth.UserId
import lgajowy.shop.domain.cart.{ CartItem, CartTotal }
import lgajowy.shop.domain.checkout.Card
import lgajowy.shop.domain.order.{ OrderError, OrderId, PaymentError, PaymentId }
import lgajowy.shop.domain.payment.Payment
import lgajowy.shop.effects.Background
import lgajowy.shop.services.{ Orders, ShoppingCart }
import org.typelevel.log4cats.Logger
import retry.RetryDetails._
import retry.RetryPolicies._
import retry._
import squants.Money

import scala.concurrent.duration._

final case class Checkout[F[_]: MonadThrow: Temporal: Background: Logger](
  paymentClient: PaymentClient[F],
  shoppingCart: ShoppingCart[F],
  orders: Orders[F]
) {

  def checkout(userId: UserId, card: Card): F[OrderId] = {
    shoppingCart.get(userId).flatMap {
      case CartTotal(items, total) =>
        for {
          paymentId <- processPayment(Payment(userId, total, card))
          orderId   <- createOrder(userId, paymentId, items, total)
          _         <- shoppingCart.delete(userId)
        } yield orderId
    }
  }

  private def processPayment(payment: Payment): F[PaymentId] = {
    val action = retryingOnAllErrors[PaymentId](retryPolicy, logError("Payments"))(paymentClient.process(payment))
    action.adaptError { case e => PaymentError(Option(e.getMessage).getOrElse("Unknown")) }
  }

  private def createOrder(userId: UserId, paymentId: PaymentId, items: List[CartItem], total: Money): F[OrderId] = {
    val action =
      retryingOnAllErrors[OrderId](retryPolicy, logError("Orders"))(orders.create(userId, paymentId, items, total))

    def backgroundAction(fa: F[OrderId]): F[OrderId] = {
      fa.adaptError { case e => OrderError(Option(e.getMessage).getOrElse("Unknown")) }
        .onError {
          case _ =>
            Logger[F].error(s"Failed to create order for: $paymentId") *>
              Background[F].schedule(backgroundAction(fa), 1.hour)
        }
    }
    backgroundAction(action)
  }

  private val retryPolicy: RetryPolicy[F] = limitRetries[F](3) meet exponentialBackoff[F](10.milliseconds)

  private def logError(action: String)(e: Throwable, details: RetryDetails): F[Unit] = details match {
    case r: WillDelayAndRetry => Logger[F].error(s"Failed on $action. We retried ${r.retriesSoFar} times.")
    case g: GivingUp          => Logger[F].error(s"Giving up on $action after ${g.totalRetries} retries. Error: $e")
  }
}
