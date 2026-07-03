package com.iti.pocketshop.common.favorites.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_products")
    fun getFavorites(): Flow<List<FavoriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavorite(products: List<FavoriteProductEntity>)

    @Delete
    suspend fun deleteFavorite(product: FavoriteProductEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :productId AND userId = :userId)")
    fun isFavorite(productId: String, userId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :productId AND userId = :userId)")
    suspend fun isFavoriteOnce(productId: String, userId: String): Boolean

    @Query("SELECT * FROM favorite_products WHERE userId = :userId")
    suspend fun getFavoritesOnce(userId: String): List<FavoriteProductEntity>

    @Query("DELETE FROM favorite_products")
    suspend fun clearFavorites()
}
