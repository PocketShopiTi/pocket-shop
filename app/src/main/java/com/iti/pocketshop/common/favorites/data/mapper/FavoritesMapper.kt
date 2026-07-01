package com.iti.pocketshop.common.favorites.data.mapper

import com.iti.pocketshop.common.favorites.data.local.FavoriteProductEntity
import com.iti.pocketshop.common.favorites.data.remote.FirebaseFavoriteProduct
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct


fun FavoriteProductEntity.toDomain(): FavoriteProduct {
    return FavoriteProduct(
        id = id,
        title = title,
        imageUrl = imageUrl,
    )
}

fun FavoriteProduct.toEntity(userId: String): FavoriteProductEntity {
    return FavoriteProductEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        userId = userId
    )
}

fun FavoriteProductEntity.toFirebaseFavorite(): FirebaseFavoriteProduct {
    return FirebaseFavoriteProduct(
        id = id.replace("/","-"),
        title = title,
        imageUrl = imageUrl,
        userId = userId
    )
}

fun FirebaseFavoriteProduct.toEntity(): FavoriteProductEntity {
    return FavoriteProductEntity(
        id = id?.replace("-","/") ?: "",
        title = title ?: "",
        imageUrl = imageUrl ?: "",
        userId = userId ?: "",
    )
}