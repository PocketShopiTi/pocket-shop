package com.iti.pocketshop.features.home.domain.models


data class Product(
    val id: String,
    val title: String,
    val handle: String,
    val vendor: String,
    val availableForSale: Boolean,
    val price: Money,
    val compareAtPrice: Money?,
    val imageUrl: String?,
    val imageAlt: String?
)

