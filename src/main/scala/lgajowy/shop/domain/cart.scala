package lgajowy.shop.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.estatico.newtype.macros.newtype
import lgajowy.shop.domain.item.{Item, ItemId}
import squants.market.Money

object cart {

  @derive(decoder, encoder, eqv, show)
  @newtype case class Quantity(value: Int)

  @derive(decoder, encoder, eqv, show)
  @newtype case class Cart(items: Map[ItemId, Quantity])

  @derive(decoder, encoder, eqv, show)
  case class CartItem(item: Item, quantity: Quantity)

  @derive(decoder, encoder, eqv, show)
  case class CartTotal(items: List[CartItem], total: Money)
}
