package com.iti.pocketshop.features.cart.domain.entity

data class ShopifyCart(
    val id: String,
    val checkoutUrl: String,
    val lines: List<CartLineItem>,
    val subtotalAmount: Double,
    val subtotalCurrencyCode: String,
    val totalAmount: Double,
    val totalCurrencyCode: String,
    val totalQuantity: Int
)
