package com.iti.pocketshop.features.cart.data.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
import com.iti.pocketshop.features.cart.data.datasource.ShopifyCartDataSource
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import com.iti.pocketshop.shopify.fragment.CartFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: ShopifyCartDataSource
) : CartRepository {

    private val _cartState = MutableStateFlow<ShopifyCart?>(null)
    override val cartState: StateFlow<ShopifyCart?> = _cartState.asStateFlow()

    private fun handleResult(result: PocketResult<CartFields, PocketDataError>): PocketResult<ShopifyCart, PocketDataError> {
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val domainCart = result.data.toDomain()
                _cartState.value = domainCart
                PocketResult.Success(domainCart)
            }
        }
    }

    override suspend fun createCart(): PocketResult<String, PocketDataError> {
        return when (val result = remoteDataSource.createCart()) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val domainCart = result.data.toDomain()
                _cartState.value = domainCart
                PocketResult.Success(domainCart.id)
            }
        }
    }

    override suspend fun loadCart(cartId: String): PocketResult<ShopifyCart, PocketDataError> {
        return handleResult(remoteDataSource.getCart(cartId))
    }

    override suspend fun addLines(
        cartId: String,
        variantId: String,
        quantity: Int
    ): PocketResult<ShopifyCart, PocketDataError> {
        return handleResult(remoteDataSource.addLines(cartId, variantId, quantity))
    }

    override suspend fun removeLines(
        cartId: String,
        lineIds: List<String>
    ): PocketResult<ShopifyCart, PocketDataError> {
        return handleResult(remoteDataSource.removeLines(cartId, lineIds))
    }

    override suspend fun updateLines(
        cartId: String,
        lineId: String,
        quantity: Int
    ): PocketResult<ShopifyCart, PocketDataError> {
        return handleResult(remoteDataSource.updateLines(cartId, lineId, quantity))
    }

    override suspend fun linkBuyerIdentity(
        cartId: String,
        customerAccessToken: String
    ): PocketResult<ShopifyCart, PocketDataError> {
        return handleResult(remoteDataSource.linkBuyerIdentity(cartId, customerAccessToken))
    }

    override fun clearLocalCart() {
        _cartState.value = null
    }
}
