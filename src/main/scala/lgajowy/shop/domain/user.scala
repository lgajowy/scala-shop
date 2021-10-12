package lgajowy.shop.domain

import lgajowy.shop.domain.auth.{UserId, UserName}

object user {
  case class User(id: UserId, name: UserName)
}
