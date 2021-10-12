package lgajowy.shop.domain

import io.estatico.newtype.macros.newtype
import lgajowy.shop.domain.brand.{Brand, BrandName}

import java.util.UUID

trait Brands[F[_]] {
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[Unit]
}

object brand {
  @newtype case class BrandId(value: UUID)

  @newtype case class BrandName(value: String)

  case class Brand(id: BrandId, name: BrandName)
}
