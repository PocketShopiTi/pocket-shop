package com.iti.pocketshop.features.categories.domain.models

data class CategoryItem(
    val id: String,
    val title: String,
    val handle: String,
    val itemCount: Int,
    val imageUrl: String?,
    val imageAlt: String?
)
