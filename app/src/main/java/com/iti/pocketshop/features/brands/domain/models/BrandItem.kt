package com.iti.pocketshop.features.brands.domain.models

data class BrandItem(
    val id: String,
    val title: String,
    val handle: String,
    val itemCount: Int,
    val imageUrl: String?,
    val imageAlt: String?
)
