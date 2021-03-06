package lgajowy.shop.http.routes.admin

import cats.MonadThrow
import cats.syntax.all._
import io.circe.JsonObject
import io.circe.syntax._
import lgajowy.shop.domain.item.CreateItemParam
import lgajowy.shop.domain.item.UpdateItemParam
import lgajowy.shop.ext.http4s.refined.RefinedRequestDecoder
import lgajowy.shop.http.auth.users.AdminUser
import lgajowy.shop.services.Items
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

final case class AdminItemRoutes[F[_]: JsonDecoder: MonadThrow](
    items: Items[F]
) extends Http4sDsl[F] {

  private[admin] val prefixPath = "/items"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      // Create new item
      case ar @ POST -> Root as _ =>
        ar.req.decodeR[CreateItemParam] { item =>
          items.create(item.toDomain).flatMap { id =>
            Created(JsonObject.singleton("item_id", id.asJson))
          }
        }

      // Update price of item
      case ar @ PUT -> Root as _ =>
        ar.req.decodeR[UpdateItemParam] { item =>
          items.update(item.toDomain) >> Ok()
        }
    }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
