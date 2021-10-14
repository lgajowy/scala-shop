package lgajowy.shop.services

import dev.profunktor.auth.jwt.JwtToken
import lgajowy.shop.domain.auth.{ Password, UserName }

trait Auth[F[_]] {
  def newUser(username: UserName, password: Password): F[JwtToken]
  def login(username: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, username: UserName): F[Unit]
}
