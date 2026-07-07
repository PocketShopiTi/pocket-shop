package com.iti.pocketshop.features.productdetails.domain.entity

data class ProductVariant(
    val id: String,
    val selectedOptionValueIds: Map<String, String>,
    val price: Money,
    val compareAtPrice: Money? = null,
    val availableForSale: Boolean,
    val imageUrl: String? = null,
)
