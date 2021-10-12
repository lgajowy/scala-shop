package lgajowy.shop.services

import lgajowy.shop.domain.auth.UserId
import lgajowy.shop.domain.cart.{Cart, CartTotal, Quantity}
import lgajowy.shop.domain.item.ItemId

trait ShoppingCart[F[_]] {
  def add(userId: UserId, itemId: ItemId, quantity: Quantity): F[Unit]
  def get(userId: UserId): F[CartTotal]
  def delete(userId: UserId): F[Unit]
  def removeItem(userId: UserId, itemId: ItemId): F[Unit]
  def update(userId: UserId, cart: Cart): F[Unit]
}