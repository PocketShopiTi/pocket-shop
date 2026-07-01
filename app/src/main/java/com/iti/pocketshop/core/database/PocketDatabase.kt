package com.iti.pocketshop.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iti.pocketshop.common.favorites.data.local.FavoriteDao
import com.iti.pocketshop.common.favorites.data.local.FavoriteProductEntity

@Database(
    entities = [FavoriteProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PocketDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
