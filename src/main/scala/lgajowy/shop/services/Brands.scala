package lgajowy.shop.services

import lgajowy.shop.domain.brand.{Brand, BrandId, BrandName}

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
