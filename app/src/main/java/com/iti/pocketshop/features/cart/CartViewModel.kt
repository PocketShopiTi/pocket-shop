package com.iti.pocketshop.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.cart.data.MockCartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: MockCartRepository
) : ViewModel() {

    private val _internalState = MutableStateFlow(CartState())
    
    val state = combine(
        cartRepository.cartItems,
        _internalState
    ) { items, internalState ->
        val subTotal = items.sumOf { it.price * it.quantity }
        val discount = subTotal * 0.10 // 10% discount
        val total = subTotal - discount
        
        internalState.copy(
            items = items,
            subTotal = subTotal,
            discount = discount,
            shipping = 0.0,
            total = total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CartState()
    )

    fun onAction(action: CartAction) {
        when (action) {
            is CartAction.UpdateQuantity -> cartRepository.updateQuantity(action.id, action.quantity)
            is CartAction.RemoveItemClicked -> {
                _internalState.update { it.copy(itemToRemove = action.item) }
            }
            CartAction.ConfirmRemoveItem -> {
                _internalState.value.itemToRemove?.let {
                    cartRepository.removeFromCart(it.id)
                }
                _internalState.update { it.copy(itemToRemove = null) }
            }
            CartAction.CancelRemoveItem -> {
                _internalState.update { it.copy(itemToRemove = null) }
            }
            CartAction.StartShoppingClicked -> {
                // Handled in UI navigation
            }
            CartAction.CheckoutClicked -> {
                cartRepository.clearCart() // Mock checkout success
            }
        }
    }
}