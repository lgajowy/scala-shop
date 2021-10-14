package lgajowy.shop.http.routes.auth

import cats.Monad
import cats.syntax.all._
import dev.profunktor.auth.AuthHeaders
import lgajowy.shop.http.auth.users.CommonUser
import lgajowy.shop.services.Auth
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

final case class LogoutRoutes[F[_]: Monad](
  auth: Auth[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/auth"

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of {
    case authedRequest @ POST -> Root / "logout" as user => {
      AuthHeaders
        .getBearerToken(authedRequest.req)
        .traverse(auth.logout(_, user.value.name)) *> NoContent()
    }
  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )
}
