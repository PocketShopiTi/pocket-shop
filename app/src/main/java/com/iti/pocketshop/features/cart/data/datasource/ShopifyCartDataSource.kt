package com.iti.pocketshop.features.cart.data.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.shopify.fragment.CartFields

interface ShopifyCartDataSource {
    suspend fun createCart(): PocketResult<CartFields, PocketDataError.Remote>
    suspend fun getCart(cartId: String): PocketResult<CartFields, PocketDataError.Remote>
    suspend fun addLines(cartId: String, variantId: String, quantity: Int): PocketResult<CartFields, PocketDataError.Remote>
    suspend fun removeLines(cartId: String, lineIds: List<String>): PocketResult<CartFields, PocketDataError.Remote>
    suspend fun updateLines(cartId: String, lineId: String, quantity: Int): PocketResult<CartFields, PocketDataError.Remote>
    suspend fun linkBuyerIdentity(cartId: String, customerAccessToken: String): PocketResult<CartFields, PocketDataError.Remote>
}
