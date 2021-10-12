package lgajowy.shop.services

import cats.data.NonEmptyList
import lgajowy.shop.domain.auth.UserId
import lgajowy.shop.domain.cart.CartItem
import lgajowy.shop.domain.order.{ Order, OrderId, PaymentId }
import squants.market.Money

trait Orders[F[_]] {

  def get(userId: UserId, orderId: OrderId): F[Option[Order]]

  def findBy(userId: UserId): F[List[Order]]
  def create(userId: UserId, paymentId: PaymentId, items: NonEmptyList[CartItem], total: Money): F[OrderId]

}
