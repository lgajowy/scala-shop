package lgajowy.shop.http.routes.admin

import cats.MonadThrow
import cats.syntax.all._
import io.circe.JsonObject
import io.circe.syntax._
import lgajowy.shop.ext.http4s.refined.RefinedRequestDecoder
import lgajowy.shop.http.auth.users.AdminUser
import lgajowy.shop.services.Categories
import lgajowy.shop.domain.category.CategoryParam
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

final case class AdminCategoryRoutes[F[_]: JsonDecoder: MonadThrow](
    categories: Categories[F]
) extends Http4sDsl[F] {

  private[admin] val prefixPath = "/categories"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      case ar @ POST -> Root as _ =>
        ar.req.decodeR[CategoryParam] { c =>
          categories.create(c.toDomain).flatMap { id =>
            Created(JsonObject.singleton("category_id", id.asJson))
          }
        }
    }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
