package lgajowy.shop.domain

import lgajowy.shop.domain.cart.Quantity
import lgajowy.shop.domain.item.ItemId

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money

import java.util.UUID
import scala.util.control.NoStackTrace

object order {
  @derive(decoder, encoder, eqv, show)
  @newtype case class OrderId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype case class PaymentId(value: UUID)

  @derive(decoder, encoder)
  case class Order(id: OrderId, pid: PaymentId, items: Map[ItemId, Quantity], total: Money)

  sealed trait OrderOrPaymentError extends NoStackTrace {
    def cause: String
  }

  case class PaymentError(cause: String) extends OrderOrPaymentError
  case class OrderError(cause: String) extends OrderOrPaymentError
}
