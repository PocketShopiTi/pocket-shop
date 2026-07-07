package com.iti.pocketshop.features.cart

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem

@Immutable
data class CartState(
    val cartId: String? = null,
    val items: List<CartLineItem> = emptyList(),
    val subTotal: Double = 0.0,
    val currencyCode: String = "",
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val itemToRemove: CartLineItem? = null,
    val isLoading: Boolean = false,
    val itemsCounts: Int = 0,
    val appliedDiscountCodes: List<String> = emptyList()
)
