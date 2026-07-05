package com.iti.pocketshop.features.cart.domain.entity

data class CartLineItem(
    val lineId: String,
    val variantId: String,
    val productId: String,
    val title: String,
    val variantTitle: String,
    val quantity: Int,
    val price: Double,
    val currencyCode: String,
    val imageUrl: String
)
