package lgajowy.shop.domain

import io.estatico.newtype.macros.newtype
import lgajowy.shop.domain.cart.Quantity
import lgajowy.shop.domain.item.ItemId
import squants.Money

import java.util.UUID
import scala.util.control.NoStackTrace

object order {
  @newtype case class OrderId(value: UUID)

  @newtype case class PaymentId(value: UUID)

  case class Order(id: OrderId, pid: PaymentId, items: Map[ItemId, Quantity], total: Money)

  sealed trait OrderOrPaymentError extends NoStackTrace {
    def cause: String
  }

  case class PaymentError(cause: String) extends OrderOrPaymentError
}
