package com.iti.pocketshop.features.cart.data.datasource

import com.iti.pocketshop.features.cart.data.local.CartDao
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.data.mapper.toEntity
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class CartLocalDataSourceImpl @Inject constructor(
    private val cartDao: CartDao
) : CartLocalDataSource {

    override suspend fun getCartItemByOnce(lineId: String): CartLineItem? {
        return cartDao.getCartItemByOnce(lineId)?.toDomain()
    }

    override fun getCartItemById(lineId: String): Flow<CartLineItem?> {
        return cartDao.getCartItemById(lineId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun deleteCartItem(lineId: String) {
        cartDao.deleteCartItem(lineId)
    }

    override suspend fun clearCart() {
        cartDao.clearCartItems()
        cartDao.clearCartMetadata()
    }

    override suspend fun updateCartItem(item: CartLineItem) {
        cartDao.updateCartItem(item.toEntity())
    }

    override fun getLocalCart(): Flow<ShopifyCart?> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }.combine(cartDao.getCartMetadata()) { items, cartEntity ->
            cartEntity?.toDomain(items)
        }
    }

    override suspend fun saveCartLocally(
        shopifyCart: ShopifyCart
    ) {
        cartDao.insertCartMetadata(
            shopifyCart.toEntity()
        )
        cartDao.insertCartItems(shopifyCart.lines.map { it.toEntity() })
    }

    override suspend fun replaceCart(shopifyCart: ShopifyCart) {
        cartDao.replaceCart(
            shopifyCart.toEntity(),
            shopifyCart.lines.map { it.toEntity() }
        )
    }

    override suspend fun saveCartItems(lineItems: List<CartLineItem>) {
        cartDao.insertCartItems(lineItems.map { it.toEntity() })
    }
}
