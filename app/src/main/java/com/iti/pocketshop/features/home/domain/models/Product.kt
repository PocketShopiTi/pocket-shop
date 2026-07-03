package com.iti.pocketshop.features.home.domain.models

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct


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

fun Product.toFavoriteProduct(): FavoriteProduct {
    return FavoriteProduct(
        id = id,
        title = title,
        imageUrl = imageUrl
    )
}