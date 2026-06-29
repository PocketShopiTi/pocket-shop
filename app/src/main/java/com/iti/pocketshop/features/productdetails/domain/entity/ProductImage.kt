package com.iti.pocketshop.features.productdetails.domain.entity

data class ProductImage(
    val id: String,
    val url: String,
    val altText: String? = null,
)
