package com.iti.pocketshop.features.home.domain.models


data class Brand(
    val id: String,
    val title: String,
    val handle: String,
    val imageUrl: String?,
    val imageAlt: String?
)