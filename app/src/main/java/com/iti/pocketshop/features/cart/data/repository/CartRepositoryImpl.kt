package com.iti.pocketshop.features.cart.data.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.cart.data.datasource.CartLocalDataSource
import com.iti.pocketshop.features.cart.data.datasource.ShopifyCartDataSource
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: ShopifyCartDataSource,
    private val localDataSource: CartLocalDataSource
) : CartRepository {

    override fun getLocalCartItems(): Flow<List<CartLineItem>> {
        return localDataSource.getCartItems()
    }

    override suspend fun createCart(): PocketResult<String, PocketDataError> {
        return remoteDataSource.createCart()
            .onSuccess {
                updateLocalCart(it.toDomain())
            }
            .map {
                it.id
            }
    }

    private suspend fun updateLocalCart(domainCart: ShopifyCart) {
        localDataSource.clearCart()
        localDataSource.saveCartItems(domainCart.lines)
    }

    override suspend fun loadCart(cartId: String): PocketResult<ShopifyCart, PocketDataError> {
        return remoteDataSource.getCart(cartId)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
    }

    override suspend fun addLines(
        cartId: String,
        variantId: String,
        quantity: Int
    ): PocketResult<ShopifyCart, PocketDataError> {
        return remoteDataSource.addLines(cartId, variantId, quantity)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
    }

    override suspend fun removeLines(
        cartId: String,
        lineIds: List<String>
    ): PocketResult<ShopifyCart, PocketDataError> {
        return remoteDataSource.removeLines(cartId, lineIds)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
    }

    override suspend fun updateLines(
        cartId: String,
        lineId: String,
        quantity: Int
    ): PocketResult<ShopifyCart, PocketDataError> {
        return remoteDataSource.updateLines(cartId, lineId, quantity)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
    }

    override suspend fun linkBuyerIdentity(
        cartId: String,
        customerAccessToken: String
    ): PocketResult<ShopifyCart, PocketDataError> {
        return remoteDataSource.linkBuyerIdentity(cartId, customerAccessToken)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
    }

    override suspend fun syncCart(cartId: String): PocketResult<ShopifyCart, PocketDataError> {
        return loadCart(cartId)
    }

    override suspend fun clearLocalCart() {
        localDataSource.clearCart()
    }
}
