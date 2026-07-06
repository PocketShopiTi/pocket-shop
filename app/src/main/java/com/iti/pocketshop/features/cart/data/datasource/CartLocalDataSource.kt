package com.iti.pocketshop.features.cart.data.datasource

import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import kotlinx.coroutines.flow.Flow

interface CartLocalDataSource {
    suspend fun getCartItemByOnce(lineId: String): CartLineItem?
    fun getCartItemById(lineId: String): Flow<CartLineItem?>
    suspend fun deleteCartItem(lineId: String)
    suspend fun clearCart()
    suspend fun updateCartItem(item: CartLineItem)

    fun getLocalCart(): Flow<ShopifyCart?>
    suspend fun saveCartLocally(
        shopifyCart: ShopifyCart
    )

    suspend fun saveCartItems(lineItems: List<CartLineItem>)
}
