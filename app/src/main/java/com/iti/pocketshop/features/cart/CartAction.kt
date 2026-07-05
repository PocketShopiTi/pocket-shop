package com.iti.pocketshop.features.cart

import com.iti.pocketshop.features.cart.domain.entity.CartLineItem

sealed interface CartAction {
    data class UpdateQuantity(val lineId: String, val quantity: Int) : CartAction
    data class RemoveItemClicked(val item: CartLineItem) : CartAction
    data object ConfirmRemoveItem : CartAction
    data object CancelRemoveItem : CartAction
    data object StartShoppingClicked : CartAction
    data object CheckoutClicked : CartAction
    data object CheckoutHandled : CartAction
    data object ErrorHandled : CartAction
}