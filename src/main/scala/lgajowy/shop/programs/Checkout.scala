package lgajowy.shop.programs

import cats.MonadThrow
import cats.effect.kernel.Temporal
import cats.implicits._
import lgajowy.shop.clients.PaymentClient
import lgajowy.shop.domain.auth.UserId
import lgajowy.shop.domain.cart.CartTotal
import lgajowy.shop.domain.checkout.Card
import lgajowy.shop.domain.order.{ OrderId, PaymentError, PaymentId }
import lgajowy.shop.domain.payment.Payment
import lgajowy.shop.services.{ Orders, ShoppingCart }
import org.typelevel.log4cats.Logger
import retry.RetryDetails._
import retry.RetryPolicies._
import retry._

import scala.concurrent.duration._

final case class Checkout[F[_]: MonadThrow: Temporal: Logger](
  paymentClient: PaymentClient[F],
  shoppingCart: ShoppingCart[F],
  orders: Orders[F]
) {

  def checkout(userId: UserId, card: Card): F[OrderId] = {
    shoppingCart.get(userId).flatMap {
      case CartTotal(items, total) =>
        for {
          paymentId <- processPayment(Payment(userId, total, card))
          orderId   <- orders.create(userId, paymentId, items, total)
          _         <- shoppingCart.delete(userId)
        } yield orderId
    }
  }

  private def processPayment(payment: Payment): F[PaymentId] = {
    val action = retryingOnAllErrors[PaymentId](retryPolicy, logError("Payments"))(paymentClient.process(payment))
    action.adaptError { case e => PaymentError(Option(e.getMessage).getOrElse("Unknown")) }
  }
  private val retryPolicy: RetryPolicy[F] = limitRetries[F](3) meet exponentialBackoff[F](10.milliseconds)

  private def logError(action: String)(e: Throwable, details: RetryDetails): F[Unit] = details match {
    case r: WillDelayAndRetry => Logger[F].error(s"Failed on $action. We retried ${r.retriesSoFar} times.")
    case g: GivingUp          => Logger[F].error(s"Giving up on $action after ${g.totalRetries} retries. Error: $e")
  }
}
