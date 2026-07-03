package com.iti.pocketshop.features.cart

sealed interface CartAction {
    data class CartIdChanged(val value: String) : CartAction
    data class CouponCodeChanged(val value: String) : CartAction
    data object ApplyCouponClicked : CartAction
}
