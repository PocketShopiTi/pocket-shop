package com.iti.pocketshop.features.cart

import com.iti.pocketshop.features.cart.domain.entity.CartItem

sealed interface CartAction {
    data class UpdateQuantity(val id: String, val quantity: Int) : CartAction
    data class RemoveItemClicked(val item: CartItem) : CartAction
    data object ConfirmRemoveItem : CartAction
    data object CancelRemoveItem : CartAction
    data object StartShoppingClicked : CartAction
    data object CheckoutClicked : CartAction
}