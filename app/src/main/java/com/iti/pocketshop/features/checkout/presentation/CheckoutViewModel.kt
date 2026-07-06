package com.iti.pocketshop.features.checkout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.address.domain.usecase.GetAddressesUseCase
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import com.iti.pocketshop.features.checkout.domain.usecases.PlaceOrderUseCase
import com.iti.pocketshop.features.checkout.domain.usecases.SetDeliveryAddressUseCase
import com.iti.pocketshop.features.coupons.domain.usecase.ApplyCouponUseCase
import com.iti.pocketshop.features.coupons.domain.usecase.RemoveCouponUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val setDeliveryAddressUseCase: SetDeliveryAddressUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val removeCouponUseCase: RemoveCouponUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    cartRepository: CartRepository
) : ViewModel() {

    var loadedInitialData = false
    private val _state = MutableStateFlow(CheckoutState())

    val state = _state
        .combine(
            cartRepository.cartState
        ) { internalState, shopifyCart ->
            internalState.copy(
                cart = shopifyCart
            )
        }
        .onStart {
            if (!loadedInitialData) {
                loadAddresses()
                loadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CheckoutState()
        )

    private fun loadAddresses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAddressesUseCase()
                .onSuccess { addressBook ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            addresses = addressBook.addresses,
                            selectedAddress = addressBook.addresses.firstOrNull()
                        )
                    }
                }
                .onError {
                    //todo handle error
                }
        }
    }

    fun onAction(action: CheckoutAction) {
        when (action) {
            is CheckoutAction.OnCouponInputChanged -> updateCouponInput(action.code)
            CheckoutAction.ApplyCoupon -> applyCoupon()
            is CheckoutAction.RemoveCoupon -> removeCoupon()

            is CheckoutAction.SelectAddress -> {
                _state.update { it.copy(selectedAddress = action.address) }
            }

            is CheckoutAction.OnPaymentSuccess -> {
                viewModelScope.launch {
                    val cartId = state.value.cart?.id ?: return@launch
                    val selectedAddress = state.value.selectedAddress ?: return@launch
                    _state.update { it.copy(isLoading = true) }
                    setDeliveryAddressUseCase(cartId, selectedAddress.id)
                        .onSuccess { cart ->
                            cart ?: run {
                                _state.update {
                                    it.copy(
                                        isLoading = false
                                    )
                                }
                                ErrorDialogController.sendEvent(PocketDataError.Remote.EMPTY_RESULT)
                                return@onSuccess
                            }
                            placeOrderUseCase(
                                cart = cart,
                                shippingAddress = selectedAddress,
                                customer = action.customer,
                                payment = action.paymentConfirmation
                            )
                                .onSuccess { newOrder ->
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            placedOrder = newOrder
                                        )
                                    }
                                }
                                .onError {
                                    ErrorDialogController.sendEvent(it)
                                }
                        }
                        .onError {
                            ErrorDialogController.sendEvent(it)
                        }

                }
            }
        }
    }

    private fun removeCoupon() {
        viewModelScope.launch {
            val cartId = state.value.cart?.id ?: return@launch
            _state.update { it.copy(isLoading = true) }
            removeCouponUseCase(cartId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            appliedCouponCode = ""
                        )
                    }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }

        }
    }

    private fun applyCoupon() {
        viewModelScope.launch {
            val cartId = state.value.cart?.id ?: return@launch
            val code = state.value.couponCodeInput.trim()
            if (code.isEmpty()) return@launch
            _state.update { it.copy(isLoading = true) }
            applyCouponUseCase(
                cartId = cartId,
                discountCodes = listOf(code)
            )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            couponCodeInput = "",
                            appliedCouponCode = code
                        )
                    }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun updateCouponInput(newCode: String) {
        _state.update { it.copy(couponCodeInput = newCode) }
    }
}