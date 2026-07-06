package com.iti.pocketshop.features.cart.domain.entity

import com.iti.pocketshop.features.cart.data.mapper.SubtotalAmount
import com.iti.pocketshop.features.cart.data.mapper.TotalAmount

data class ShopifyCart(
    val id: String,
    val lines: List<CartLineItem>,
    val subtotalAmount: SubtotalAmount,
    val totalAmount: TotalAmount,
    val totalQuantity: Int,
    val appliedDiscountCodes: List<String> = emptyList()
)
