package com.iti.pocketshop.features.ordercheckout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import com.iti.pocketshop.features.checkout.domain.repository.CheckoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderCheckoutViewModel @Inject constructor(
    private val checkoutRepository: CheckoutRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _internalState = MutableStateFlow(OrderCheckoutState())
    
    val state = combine(
        cartRepository.cartState,
        _internalState
    ) { shopifyCart, internalState ->
        internalState.copy(
            cart = shopifyCart
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = OrderCheckoutState()
    )

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            _internalState.update { it.copy(isLoading = true) }
            val result = checkoutRepository.getCustomerAddresses()
            result.onSuccess { addresses ->
                _internalState.update { 
                    it.copy(
                        isLoading = false, 
                        addresses = addresses,
                        selectedAddressId = addresses.firstOrNull()?.id
                    ) 
                }
            }.onFailure { error ->
                _internalState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun onAction(action: OrderCheckoutAction) {
        when (action) {
            is OrderCheckoutAction.OnCouponInputChanged -> {
                _internalState.update { it.copy(couponCodeInput = action.code) }
            }
            OrderCheckoutAction.ApplyCoupon -> {
                val cartId = state.value.cart?.id ?: return
                val code = state.value.couponCodeInput.trim()
                if (code.isEmpty()) return
                
                viewModelScope.launch {
                    _internalState.update { it.copy(isLoading = true) }
                    val result = checkoutRepository.applyCoupon(cartId, code)
                    result.onSuccess { updatedCart ->
                        _internalState.update { it.copy(isLoading = false, couponCodeInput = "") }
                        // The cartRepository.cartState should ideally be updated, but since this is a simple UI 
                        // and cartRepository is the single source of truth, 
                        // we'd normally update the CartRepo state. For this simple demo we'll let the next Cart API call refresh it,
                        // or we can refresh the cart via cartRepository.getCart(cartId).
                    }.onFailure {
                        _internalState.update { it.copy(isLoading = false, errorMessage = "Failed to apply coupon") }
                    }
                }
            }
            is OrderCheckoutAction.RemoveCoupon -> {
                val cartId = state.value.cart?.id ?: return
                viewModelScope.launch {
                    _internalState.update { it.copy(isLoading = true) }
                    val result = checkoutRepository.removeCoupon(cartId)
                    result.onSuccess {
                        _internalState.update { it.copy(isLoading = false) }
                    }.onFailure {
                        _internalState.update { it.copy(isLoading = false, errorMessage = "Failed to remove coupon") }
                    }
                }
            }
            is OrderCheckoutAction.SelectAddress -> {
                _internalState.update { it.copy(selectedAddressId = action.addressId) }
            }
            is OrderCheckoutAction.SelectPaymentMethod -> {
                _internalState.update { it.copy(selectedPaymentMethod = action.method) }
            }
            OrderCheckoutAction.PlaceOrder -> {
                val cartId = state.value.cart?.id ?: return
                val addressId = state.value.selectedAddressId ?: return
                
                viewModelScope.launch {
                    _internalState.update { it.copy(isLoading = true) }
                    
                    // 1. Set Delivery Address
                    val addressResult = checkoutRepository.setDeliveryAddress(cartId, addressId)
                    if (addressResult.isFailure) {
                        _internalState.update { it.copy(isLoading = false, errorMessage = "Failed to set delivery address") }
                        return@launch
                    }
                    
                    // 2. Get Checkout URL
                    val urlResult = checkoutRepository.getCheckoutUrl(cartId)
                    urlResult.onSuccess { url ->
                        _internalState.update { it.copy(isLoading = false, checkoutUrl = url) }
                    }.onFailure {
                        _internalState.update { it.copy(isLoading = false, errorMessage = "Failed to get checkout URL") }
                    }
                }
            }
            OrderCheckoutAction.AddTestAddress -> {
                viewModelScope.launch {
                    _internalState.update { it.copy(isLoading = true) }
                    val result = checkoutRepository.createTestAddress()
                    result.onSuccess {
                        loadAddresses()
                    }.onFailure { error ->
                        _internalState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
                }
            }
            OrderCheckoutAction.CheckoutHandled -> {
                _internalState.update { it.copy(checkoutUrl = null) }
            }
            OrderCheckoutAction.ErrorHandled -> {
                _internalState.update { it.copy(errorMessage = null) }
            }
        }
    }
}