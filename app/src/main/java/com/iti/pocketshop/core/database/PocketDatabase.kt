package com.iti.pocketshop.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iti.pocketshop.common.favorites.data.local.FavoriteDao
import com.iti.pocketshop.common.favorites.data.local.FavoriteProductEntity
import com.iti.pocketshop.features.cart.data.local.CartDao
import com.iti.pocketshop.features.cart.data.local.CartLineItemEntity
import com.iti.pocketshop.features.cart.data.local.ShopifyCartEntity

@Database(
    entities = [FavoriteProductEntity::class, CartLineItemEntity::class, ShopifyCartEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PocketDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cartDao(): CartDao
}
