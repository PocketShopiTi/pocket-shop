package com.iti.pocketshop.features.cart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopify_cart")
data class ShopifyCartEntity(
    @PrimaryKey val id: String,
    val subtotalAmount: Double,
    val totalAmount: Double,
    val totalQuantity: Int,
    val currencyCode: String,
    val appliedDiscountCodes: String = "",
)
