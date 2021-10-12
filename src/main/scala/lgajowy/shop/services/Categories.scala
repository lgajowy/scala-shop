package lgajowy.shop.services

import lgajowy.shop.domain.category.{Category, CategoryId, CategoryName}

trait Categories[F[_]] {
  def findAll: F[List[Category]]
  def create(name: CategoryName): F[CategoryId]
}
