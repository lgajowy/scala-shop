package lgajowy.shop.services

import cats.effect.{ MonadCancelThrow, Resource }
import cats.syntax.all.none
import lgajowy.shop.domain.auth._
import lgajowy.shop.domain.user.User
import lgajowy.shop.sql.codecs._
import cats.syntax.all._
import lgajowy.shop.effects.GenUUID
import lgajowy.shop.http.auth.users.UserWithPassword
import skunk._
import skunk.implicits._

trait Users[F[_]] {
  def find(username: UserName): F[Option[UserWithPassword]]
  def create(username: UserName, password: EncryptedPassword): F[UserId]
}

object Users {
  def make[F[_]: GenUUID: MonadCancelThrow](
    postgres: Resource[F, Session[F]]
  ): Users[F] =
    new Users[F] {
      import UserSQL._

      def find(username: UserName): F[Option[UserWithPassword]] =
        postgres.use { session =>
          session.prepare(selectUser).use { q =>
            q.option(username).map {
              case Some(u ~ p) => UserWithPassword(u.id, u.name, p).some
              case _           => none[UserWithPassword]
            }
          }
        }

      def create(username: UserName, password: EncryptedPassword): F[UserId] =
        postgres.use { session =>
          session.prepare(insertUser).use { cmd =>
            GenUUID[F].make.flatMap { id =>
              val userId = UserId(id)
              cmd
                .execute(User(userId, username) ~ password)
                .as(userId)
                .recoverWith {
                  case SqlState.UniqueViolation(_) =>
                    UserNameInUse(username).raiseError[F, UserId]
                }
            }
          }
        }
    }

}

private object UserSQL {

  val codec: Codec[User ~ EncryptedPassword] =
    (userId ~ userName ~ encPassword).imap {
      case i ~ n ~ p =>
        User(i, n) ~ p
    } {
      case u ~ p =>
        u.id ~ u.name ~ p
    }

  val selectUser: Query[UserName, User ~ EncryptedPassword] =
    sql"""
        SELECT * FROM users
        WHERE name = $userName
       """.query(codec)

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""
        INSERT INTO users
        VALUES ($codec)
        """.command

}
