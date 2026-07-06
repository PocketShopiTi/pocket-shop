package com.iti.pocketshop.features.cart.data.datasource

import com.iti.pocketshop.features.cart.data.local.CartDao
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.data.mapper.toEntity
import com.iti.pocketshop.features.cart.domain.entity.CartLineItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartLocalDataSourceImpl @Inject constructor(
    private val cartDao: CartDao
) : CartLocalDataSource {
    override fun getCartItems(): Flow<List<CartLineItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCartItemById(lineId: String): Flow<CartLineItem> {
        return cartDao.getCartItemById(lineId).map { entity ->
            entity.toDomain()
        }
    }

    override suspend fun saveCartItems(items: List<CartLineItem>) {
        cartDao.insertCartItems(items.map { it.toEntity() })
    }

    override suspend fun deleteCartItem(lineId: String) {
        cartDao.deleteCartItem(lineId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override suspend fun updateCartItem(item: CartLineItem) {
        cartDao.updateCartItem(item.toEntity())
    }
}
