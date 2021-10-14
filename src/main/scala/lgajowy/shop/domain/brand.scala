package lgajowy.shop.domain

import lgajowy.shop.domain.brand.{ Brand, BrandName }
import lgajowy.shop.ext.http4s.queryParam
import lgajowy.shop.ext.http4s.refined._

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.circe.{ Decoder, Encoder }
import io.estatico.newtype.macros.newtype

import java.util.UUID

trait Brands[F[_]] {
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[Unit]
}

object brand {
  @derive(decoder, encoder, eqv, show)
  @newtype case class BrandId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype case class BrandName(value: String)

  @derive(decoder, encoder, eqv, show)
  case class Brand(id: BrandId, name: BrandName)

  @derive(queryParam, show)
  @newtype
  case class BrandParam(value: NonEmptyString) {
    def toDomain: BrandName = BrandName(value.toLowerCase.capitalize)
  }

  object BrandParam {
    implicit val jsonEncoder: Encoder[BrandParam] =
      Encoder.forProduct1("name")(_.value)

    implicit val jsonDecoder: Decoder[BrandParam] =
      Decoder.forProduct1("name")(BrandParam.apply)
  }
}
