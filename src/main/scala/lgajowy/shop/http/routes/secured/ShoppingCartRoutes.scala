package lgajowy.shop.http.routes.secured

import lgajowy.shop.http.auth.users.CommonUser
import lgajowy.shop.services.ShoppingCart
import lgajowy.shop.domain.cart.Cart
import cats.Monad
import cats.syntax.all._
import lgajowy.shop.http.vars.ItemIdVar
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

class ShoppingCartRoutes[F[_]: JsonDecoder: Monad](
  shoppingCart: ShoppingCart[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/cart"

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of {

    case GET -> Root as user => Ok(shoppingCart.get(user.value.id))

    case ar @ POST -> Root as user =>
      ar.req
        .asJsonDecode[Cart]
        .flatMap(
          _.items.toList.traverse { case (id, quantity) => shoppingCart.add(user.value.id, id, quantity) } *> Created()
        )

    case ar @ PUT -> Root as user =>
      ar.req
        .asJsonDecode[Cart]
        .flatMap(
          shoppingCart.update(user.value.id, _) *> Ok()
        )

    case DELETE -> Root / ItemIdVar(itemId) as user =>
      shoppingCart.removeItem(user.value.id, itemId) *> NoContent()
  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )
}
