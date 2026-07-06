package com.iti.pocketshop.features.cart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartLineItemEntity(
    @PrimaryKey val lineId: String,
    val variantId: String,
    val productId: String,
    val title: String,
    val variantTitle: String,
    val quantity: Int,
    val price: Double,
    val currencyCode: String,
    val imageUrl: String
)
