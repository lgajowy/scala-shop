package lgajowy.shop.domain

import lgajowy.shop.domain.auth.UserId
import lgajowy.shop.domain.checkout.Card
import squants.market.Money

object payment {
  case class Payment(id: UserId, total: Money, card: Card)
}
