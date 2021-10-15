package lgajowy.shop.http

import cats.implicits._
import lgajowy.shop.domain.item.ItemId

import java.util.UUID

object vars {
  protected class UUIDVar[A](f: UUID => A) {
    def unapply(str: String): Option[A] =
      Either.catchNonFatal(f(UUID.fromString(str))).toOption
  }

  object ItemIdVar extends UUIDVar(ItemId.apply)
}
