package com.iti.pocketshop.features.cart

import com.iti.pocketshop.features.cart.domain.entity.CartLineItem

sealed interface CartAction {
    data object FetchCart: CartAction
    data class UpdateQuantity(val lineId: String, val quantity: Int) : CartAction
    data class PrepareDeletingItem(val item: CartLineItem) : CartAction
    data object ConfirmRemoveItem : CartAction
    data object CancelDeletingItem : CartAction
}