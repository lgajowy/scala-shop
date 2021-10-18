package lgajowy.shop.services

import cats.effect.{ MonadCancelThrow, Resource }
import lgajowy.shop.domain.category.{ Category, CategoryId, CategoryName }
import cats.syntax.all._
import lgajowy.shop.effects.GenUUID
import skunk._
import skunk.implicits._
import lgajowy.shop.sql.codecs._

trait Categories[F[_]] {
  def findAll: F[List[Category]]
  def create(name: CategoryName): F[CategoryId]
}

object Categories {
  def make[F[_]: GenUUID: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): Categories[F] =
    new Categories[F] {
      import CategorySQL._

      def findAll: F[List[Category]] =
        postgres.use(_.execute(selectAll))

      def create(name: CategoryName): F[CategoryId] =
        postgres.use { session =>
          session.prepare(insertCategory).use { cmd =>
            GenUUID[F].make.flatMap { id =>
              val categoryId: CategoryId = CategoryId(id)
              cmd.execute(Category(categoryId, name)).as(categoryId)
            }
          }
        }
    }
}

private object CategorySQL {

  val codec: Codec[Category] =
    (categoryId ~ categoryName).imap {
      case i ~ n => Category(i, n)
    }(c => c.uuid ~ c.name)

  val selectAll: Query[Void, Category] =
    sql"""
        SELECT * FROM categories
       """.query(codec)

  val insertCategory: Command[Category] =
    sql"""
        INSERT INTO categories
        VALUES ($codec)
        """.command

}
