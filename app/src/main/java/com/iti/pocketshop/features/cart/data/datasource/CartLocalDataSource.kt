package com.iti.pocketshop.features.cart.data.datasource

import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import kotlinx.coroutines.flow.Flow

interface CartLocalDataSource {
    fun getCartItems(): Flow<List<CartLineItem>>
    fun getCartItemById(lineId: String): Flow<CartLineItem>
    suspend fun saveCartItems(items: List<CartLineItem>)
    suspend fun deleteCartItem(lineId: String)
    suspend fun clearCart()
    suspend fun updateCartItem(item: CartLineItem)
}
