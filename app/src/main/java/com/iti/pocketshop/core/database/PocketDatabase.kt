package com.iti.pocketshop.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iti.pocketshop.features.wishlist.data.local.FavoriteProductEntity
import com.iti.pocketshop.features.wishlist.data.local.FavoriteDao

@Database(
    entities = [FavoriteProductEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PocketDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
