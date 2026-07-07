package com.iti.pocketshop.features.cart.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getLocalCart(): Flow<ShopifyCart?>

    suspend fun createCart(): PocketResult<String, PocketDataError>
    suspend fun loadCart(cartId: String): PocketResult<ShopifyCart, PocketDataError>
    suspend fun addLines(cartId: String, variantId: String, quantity: Int): PocketResult<ShopifyCart, PocketDataError>
    suspend fun removeLines(cartId: String, lineIds: List<String>): PocketResult<ShopifyCart, PocketDataError>
    suspend fun updateLines(cartId: String, lineId: String, quantity: Int): PocketResult<ShopifyCart, PocketDataError>
    suspend fun linkBuyerIdentity(cartId: String, customerAccessToken: String): PocketResult<ShopifyCart, PocketDataError>
    suspend fun syncCart(cartId: String): PocketResult<ShopifyCart, PocketDataError>
    suspend fun clearLocalCart()

    suspend fun updateLocalItemQuantity(lineId: String, quantity: Int)
    suspend fun deleteLocalItem(lineId: String)
}
