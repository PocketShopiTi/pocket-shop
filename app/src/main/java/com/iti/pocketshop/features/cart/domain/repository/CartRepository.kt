package com.iti.pocketshop.features.cart.domain.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartState: StateFlow<ShopifyCart?>
    
    suspend fun createCart(): PocketResult<String, PocketDataError>
    suspend fun loadCart(cartId: String): PocketResult<ShopifyCart, PocketDataError>
    suspend fun addLines(cartId: String, variantId: String, quantity: Int): PocketResult<ShopifyCart, PocketDataError>
    suspend fun removeLines(cartId: String, lineIds: List<String>): PocketResult<ShopifyCart, PocketDataError>
    suspend fun updateLines(cartId: String, lineId: String, quantity: Int): PocketResult<ShopifyCart, PocketDataError>
    suspend fun linkBuyerIdentity(cartId: String, customerAccessToken: String): PocketResult<ShopifyCart, PocketDataError>
    fun clearLocalCart()
}
