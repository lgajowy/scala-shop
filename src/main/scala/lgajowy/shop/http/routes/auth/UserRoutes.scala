package lgajowy.shop.http.routes.auth

import cats.MonadThrow
import cats.syntax.all._
import lgajowy.shop.domain._
import lgajowy.shop.domain.auth._
import lgajowy.shop.ext.http4s.refined._
import lgajowy.shop.services.Auth
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class UserRoutes[F[_]: JsonDecoder: MonadThrow](
  auth: Auth[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/auth"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ POST -> Root / "users" =>
      req.decodeR[CreateUser] { userToCreate =>
        auth
          .newUser(userToCreate.username.toDomain, userToCreate.password.toDomain)
          .flatMap(Created(_))
          .recoverWith {
            case UserNameInUse(u) => Conflict(u.value)
          }
      }

  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
