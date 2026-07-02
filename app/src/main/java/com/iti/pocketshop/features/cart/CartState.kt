package com.iti.pocketshop.features.cart

import com.iti.pocketshop.features.cart.domain.entity.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val subTotal: Double = 0.0,
    val discount: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val itemToRemove: CartItem? = null
)