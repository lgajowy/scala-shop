package lgajowy.shop.http.routes.secured

import lgajowy.shop.domain.order
import lgajowy.shop.ext.http4s.refined.RefinedRequestDecoder
import lgajowy.shop.http.auth.users.CommonUser
import lgajowy.shop.http.vars.OrderIdVar
import lgajowy.shop.programs.CheckoutProgram
import cats.MonadThrow
import cats.syntax.all._
import lgajowy.shop.domain.cart.CartNotFound
import lgajowy.shop.domain.checkout.Card
import lgajowy.shop.domain.order.{ EmptyCartError, OrderOrPaymentError }
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

final case class CheckoutRoutes[F[_]: JsonDecoder: MonadThrow](
  program: CheckoutProgram[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/checkout"

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of {

    case ar @ POST -> Root as user => {
      ar.req.decodeR[Card] { card =>
        program
          .process(user.value.id, card)
          .flatMap(Created(_))
          .recoverWith {
            case CartNotFound(userId) =>
              NotFound(s"Cart not found for user: ${userId.value}")
            case EmptyCartError =>
              BadRequest("Shopping cart is empty!")
            case e: OrderOrPaymentError =>
              BadRequest(e.cause)
          }
      }
    }
  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
