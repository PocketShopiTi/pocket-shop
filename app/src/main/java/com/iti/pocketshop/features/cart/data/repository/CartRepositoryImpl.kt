package com.iti.pocketshop.features.cart.data.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.cart.data.datasource.CartLocalDataSource
import com.iti.pocketshop.features.cart.data.datasource.ShopifyCartDataSource
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: ShopifyCartDataSource,
    private val localDataSource: CartLocalDataSource
) : CartRepository {

    override fun getLocalCart(): Flow<ShopifyCart?> {
        return localDataSource.getLocalCart()
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
        localDataSource.saveCartLocally(domainCart)
    }

    override suspend fun loadCart(
        cartId: String
    ): PocketResult<ShopifyCart, PocketDataError> {
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
        val originalItems = mutableListOf<CartLineItem>()
        lineIds.forEach { lineId ->
            localDataSource.getCartItemByOnce(lineId)?.let { originalItems.add(it) }
            localDataSource.deleteCartItem(lineId)
        }

        return remoteDataSource.removeLines(cartId, lineIds)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
            .onError {
                originalItems.forEach { item ->
                    localDataSource.saveCartItems(listOf(item))
                }
            }
    }

    override suspend fun updateLines(
        cartId: String,
        lineId: String,
        quantity: Int
    ): PocketResult<ShopifyCart, PocketDataError> {
        val originalItem = localDataSource.getCartItemByOnce(lineId)
        originalItem?.copy(quantity = quantity)?.let {
            localDataSource.updateCartItem(it)
        }

        return remoteDataSource.updateLines(cartId, lineId, quantity)
            .map {
                it.toDomain()
            }
            .onSuccess {
                updateLocalCart(it)
            }
            .onError {
                originalItem?.let { item ->
                    localDataSource.updateCartItem(item)
                }
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

    override suspend fun updateLocalItemQuantity(lineId: String, quantity: Int) {
        val item = localDataSource.getCartItemById(lineId).first()
        item?.copy(quantity = quantity)?.let {
            localDataSource.updateCartItem(it)
        }
    }

    override suspend fun deleteLocalItem(lineId: String) {
        localDataSource.deleteCartItem(lineId)
    }
}
