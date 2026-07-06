package com.iti.pocketshop.features.cart.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartLineItemEntity>>

    @Query("SELECT * FROM cart_items where lineId = :lineId")
    fun getCartItemById(lineId: String): Flow<CartLineItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartLineItemEntity>)

    @Query("DELETE FROM cart_items WHERE lineId = :lineId")
    suspend fun deleteCartItem(lineId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Update
    suspend fun updateCartItem(item: CartLineItemEntity)
}
