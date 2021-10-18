package lgajowy.shop.services

import cats.effect.{ MonadCancelThrow, Resource }
import lgajowy.shop.domain.brand.{ Brand, BrandId, BrandName }
import lgajowy.shop.effects.GenUUID
import skunk._
import skunk.implicits._
import lgajowy.shop.sql.codecs._
import cats.syntax.all._

trait Brands[F[_]] {
  def findAll: F[List[Brand]]

  def create(name: BrandName): F[BrandId]
}

object Brands {
  def make[F[_]: GenUUID: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): Brands[F] =
    new Brands[F] {
      import BrandSQL._

      def findAll: F[List[Brand]] =
        postgres.use(_.execute(selectAll))

      def create(name: BrandName): F[BrandId] =
        postgres.use { session =>
          session.prepare(insertBrand).use { cmd =>
            GenUUID[F].make.flatMap { id =>
              val brandId = BrandId(id)
              cmd.execute(Brand(brandId, name)).as(brandId)
            }
          }
        }
    }
}

private object BrandSQL {

  val codec: Codec[Brand] =
    (brandId ~ brandName).imap {
      case i ~ n => Brand(i, n)
    }(b => b.uuid ~ b.name)

  val selectAll: Query[Void, Brand] =
    sql"""
        SELECT * FROM brands
       """.query(codec)

  val insertBrand: Command[Brand] =
    sql"""
        INSERT INTO brands
        VALUES ($codec)
        """.command

}
