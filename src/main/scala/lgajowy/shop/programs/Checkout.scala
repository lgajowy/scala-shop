package lgajowy.shop.programs

import cats.Monad
import cats.implicits._
import lgajowy.shop.clients.PaymentClient
import lgajowy.shop.domain.auth.UserId
import lgajowy.shop.domain.cart.CartTotal
import lgajowy.shop.domain.checkout.Card
import lgajowy.shop.domain.order.OrderId
import lgajowy.shop.domain.payment.Payment
import lgajowy.shop.services.{ Orders, ShoppingCart }

final class Checkout[F[_]: Monad](paymentClient: PaymentClient[F], shoppingCart: ShoppingCart[F], orders: Orders[F]) {

  def checkout(userId: UserId, card: Card): F[OrderId] = {
    shoppingCart.get(userId).flatMap {
      case CartTotal(items, total) =>
        for {
          paymentId <- paymentClient.process(Payment(userId, total, card))
          orderId   <- orders.create(userId, paymentId, items, total)
          _         <- shoppingCart.delete(userId)
        } yield orderId
    }
  }

}
