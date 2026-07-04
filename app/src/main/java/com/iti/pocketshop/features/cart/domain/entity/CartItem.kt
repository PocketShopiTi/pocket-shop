package com.iti.pocketshop.features.cart.domain.entity

data class CartItem(
    val id: String,
    val productId: String,
    val title: String,
    val brand: String,
    val size: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int
)
