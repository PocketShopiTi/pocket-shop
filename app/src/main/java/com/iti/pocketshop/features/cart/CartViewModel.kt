package com.iti.pocketshop.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _internalState = MutableStateFlow(CartState())
    
    val state = combine(
        cartRepository.cartState,
        _internalState
    ) { shopifyCart, internalState ->
        if (shopifyCart == null) {
            internalState.copy(items = emptyList(), subTotal = 0.0, total = 0.0)
        } else {
            internalState.copy(
                items = shopifyCart.lines,
                subTotal = shopifyCart.subtotalAmount,
                currencyCode = shopifyCart.subtotalCurrencyCode,
                shipping = 0.0,
                total = shopifyCart.totalAmount
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CartState()
    )

    fun onAction(action: CartAction) {
        when (action) {
            is CartAction.UpdateQuantity -> {
                viewModelScope.launch {
                    val cartId = cartRepository.cartState.value?.id ?: return@launch
                    _internalState.update { it.copy(isLoading = true, error = null) }
                    val result = cartRepository.updateLines(cartId, action.lineId, action.quantity)
                    if (result is PocketResult.Error) {
                        _internalState.update { it.copy(isLoading = false, error = result.error) }
                    } else {
                        _internalState.update { it.copy(isLoading = false) }
                    }
                }
            }
            is CartAction.RemoveItemClicked -> {
                _internalState.update { it.copy(itemToRemove = action.item) }
            }
            CartAction.ConfirmRemoveItem -> {
                val item = _internalState.value.itemToRemove
                val cartId = cartRepository.cartState.value?.id
                if (item != null && cartId != null) {
                    viewModelScope.launch {
                        _internalState.update { it.copy(itemToRemove = null, isLoading = true, error = null) }
                        val result = cartRepository.removeLines(cartId, listOf(item.lineId))
                        if (result is PocketResult.Error) {
                            _internalState.update { it.copy(isLoading = false, error = result.error) }
                        } else {
                            _internalState.update { it.copy(isLoading = false) }
                        }
                    }
                } else {
                    _internalState.update { it.copy(itemToRemove = null) }
                }
            }
            CartAction.CancelRemoveItem -> {
                _internalState.update { it.copy(itemToRemove = null) }
            }
            CartAction.StartShoppingClicked -> {
                // Handled in UI navigation
            }
            CartAction.CheckoutClicked -> {
                val url = cartRepository.cartState.value?.checkoutUrl
                if (url != null) {
                    _internalState.update { it.copy(checkoutUrl = url) }
                }
            }
            CartAction.CheckoutHandled -> {
                _internalState.update { it.copy(checkoutUrl = null) }
            }
            CartAction.ErrorHandled -> {
                _internalState.update { it.copy(error = null) }
            }
        }
    }
}