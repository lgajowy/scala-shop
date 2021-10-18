package lgajowy.shop.services

import lgajowy.shop.domain.brand.{Brand, BrandId, BrandName}

import skunk._
import skunk.implicits._
import lgajowy.shop.sql.codecs._

trait Brands[F[_]] {
  def findAll: F[List[Brand]]

  def create(name: BrandName): F[BrandId]
}

object Brands {
  def make[F[_]](): Brands[F] = new Brands[F] {
    override def findAll: F[List[Brand]] = ???

    override def create(name: BrandName): F[BrandId] = ???
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
