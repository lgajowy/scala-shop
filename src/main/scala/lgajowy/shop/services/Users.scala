package lgajowy.shop.services

import lgajowy.shop.domain.auth.{ Password, UserId, UserName }
import lgajowy.shop.domain.user.User

trait Users[F[_]] {
  def find(username: UserName, password: Password): F[Option[User]]
  def create(username: UserName, password: Password): F[UserId]
}
