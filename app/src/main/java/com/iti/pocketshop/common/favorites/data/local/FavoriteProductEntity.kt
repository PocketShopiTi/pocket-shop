package com.iti.pocketshop.common.favorites.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String?,
    val userId: String
)
