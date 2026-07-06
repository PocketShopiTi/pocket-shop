package com.iti.pocketshop.features.cart

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem

data class CartState(
    val items: List<CartLineItem> = emptyList(),
    val subTotal: Double = 0.0,
    val currencyCode: String = "USD",
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val itemToRemove: CartLineItem? = null,
    val checkoutUrl: String? = null,
    val isLoading: Boolean = false,
    val error: PocketDataError? = null
)