package lgajowy.shop.clients

import lgajowy.shop.domain.order.PaymentId
import lgajowy.shop.domain.payment.Payment

trait PaymentClient[F[_]] {
  def process(payment: Payment): F[PaymentId]
}
