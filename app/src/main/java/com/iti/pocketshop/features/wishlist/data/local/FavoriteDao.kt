package com.iti.pocketshop.features.wishlist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_products WHERE userId = :userId")
    fun getFavorites(userId: String): Flow<List<FavoriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteProductEntity)

    @Delete
    suspend fun deleteFavorite(product: FavoriteProductEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :productId AND userId = :userId)")
    fun isFavorite(productId: String, userId: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_products WHERE userId = :userId")
    suspend fun getFavoritesOnce(userId: String): List<FavoriteProductEntity>

    @Query("DELETE FROM favorite_products WHERE userId = :userId")
    suspend fun clearFavorites(userId: String)
}
