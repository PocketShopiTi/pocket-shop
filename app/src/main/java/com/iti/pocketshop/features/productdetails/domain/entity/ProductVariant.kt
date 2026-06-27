package com.iti.pocketshop.features.productdetails.domain.entity

data class ProductVariant(
    val id: String,
    val selectedOptionValueIds: Set<String>,
    val price: Money,
    val availableForSale: Boolean,
)
