package com.iti.pocketshop.features.cart.data

import com.iti.pocketshop.features.cart.domain.entity.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockCartRepository @Inject constructor() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(item: CartItem) {
        _cartItems.update { currentItems ->
            val existingItemIndex = currentItems.indexOfFirst { it.productId == item.productId && it.size == item.size }
            if (existingItemIndex != -1) {
                // Item exists, update quantity
                val updatedItems = currentItems.toMutableList()
                val existingItem = updatedItems[existingItemIndex]
                updatedItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
                updatedItems
            } else {
                // New item
                currentItems + item.copy(id = UUID.randomUUID().toString())
            }
        }
    }

    fun removeFromCart(id: String) {
        _cartItems.update { currentItems ->
            currentItems.filterNot { it.id == id }
        }
    }

    fun updateQuantity(id: String, quantity: Int) {
        if (quantity < 1) return
        _cartItems.update { currentItems ->
            currentItems.map { if (it.id == id) it.copy(quantity = quantity) else it }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
