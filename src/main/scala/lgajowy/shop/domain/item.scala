package lgajowy.shop.domain

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder, keyDecoder, keyEncoder}
import derevo.derive
import io.estatico.newtype.macros.newtype
import lgajowy.shop.domain.brand.{Brand, BrandId}
import lgajowy.shop.domain.category.{Category, CategoryId}
import squants.market.Money

import java.util.UUID

object item {

  @derive(decoder, encoder, keyDecoder, keyEncoder, eqv, show)
  @newtype case class ItemId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype case class ItemName(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype case class ItemDescription(value: String)

  @derive(decoder, encoder, eqv, show)
  case class Item(
    uuid: ItemId,
    name: ItemName,
    description: ItemDescription,
    price: Money,
    brand: Brand,
    category: Category
  )

  case class CreateItem(
    name: ItemName,
    description: ItemDescription,
    price: Money,
    brandId: BrandId,
    categoryId: CategoryId
  )

  @derive(decoder, encoder)
  case class UpdateItem(
    id: ItemId,
    price: Money
  )
}
