package lgajowy.shop.domain


import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe._
import io.circe.refined._
import io.estatico.newtype.macros.newtype

import java.util.UUID
import scala.util.control.NoStackTrace

object auth {
  @derive(decoder, encoder, eqv, show)
  @newtype case class UserId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype case class Password(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype case class UserName(value: String)

  @derive(decoder, encoder)
  @newtype
  case class UserNameParam(value: NonEmptyString) {
    def toDomain: UserName = UserName(value.toLowerCase)
  }

  @derive(decoder, encoder)
  @newtype
  case class PasswordParam(value: NonEmptyString) {
    def toDomain: Password = Password(value)
  }

  @derive(decoder, encoder, eqv, show)
  @newtype
  case class EncryptedPassword(value: String)

  @derive(decoder, encoder)
  case class LoginUser(
    username: UserNameParam,
    password: PasswordParam
  )

  case class UserNotFound(username: UserName)    extends NoStackTrace
  case class InvalidPassword(username: UserName) extends NoStackTrace
}
