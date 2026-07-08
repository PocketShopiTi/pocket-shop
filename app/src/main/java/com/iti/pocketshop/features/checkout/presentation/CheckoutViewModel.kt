package com.iti.pocketshop.features.checkout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.core.pricing.PriceFormatter
import com.iti.pocketshop.features.address.domain.usecase.GetAddressesUseCase
import com.iti.pocketshop.features.cart.domain.usecase.GetLocalCartUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RestoreOrCreateCartUseCase
import com.iti.pocketshop.features.checkout.data.mappers.COD_GATEWAY_NAME
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation
import com.iti.pocketshop.features.checkout.domain.usecases.PlaceOrderUseCase
import com.iti.pocketshop.features.checkout.domain.usecases.SetDeliveryAddressUseCase
import com.iti.pocketshop.features.coupons.domain.usecase.ApplyCouponUseCase
import com.iti.pocketshop.features.coupons.domain.usecase.RemoveCouponUseCase
import com.iti.pocketshop.features.home.domain.models.Money
import com.iti.pocketshop.features.payment.domain.models.UserData
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
    private val restoreOrCreateCartUseCase: RestoreOrCreateCartUseCase,
    getLocalCartUseCase: GetLocalCartUseCase,
) : ViewModel() {

    var loadedInitialData = false
    private val _state = MutableStateFlow(CheckoutState())

    val state = _state
        .combine(getLocalCartUseCase()) {
            currentState, localCart ->
            currentState.copy(cart = localCart)
        }
        .onStart {
            if (!loadedInitialData) {
                fetchData()
                loadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CheckoutState()
        )

    private fun fetchData() {
        loadAddressesAnd()
    }

    private fun loadAddressesAnd() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            launch {
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
            }
            launch {
                restoreOrCreateCartUseCase()
                    .onError {
                        ErrorDialogController.sendEvent(it)
                    }
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

            is CheckoutAction.SelectPaymentMethod -> {
                _state.update { it.copy(selectedPaymentMethod = action.method) }
            }

            is CheckoutAction.PlaceCodOrder -> placeCodOrder(action.customer)

            is CheckoutAction.OnPaymentSuccess -> {
                placeOrder(action.customer, action.paymentConfirmation)
            }
        }
    }

    private fun placeCodOrder(customer: UserData) {
        val total = state.value.cart?.totalAmount ?: return
        val paymentAmount = PriceFormatter.toEgpMoney(
            amount = total.amount,
            sourceCurrencyCode = total.currencyCode.rawValue,
        )
        placeOrder(
            customer = customer,
            payment = PaymentConfirmation(
                transactionId = null,
                gateway = COD_GATEWAY_NAME,
                amount = Money(
                    amount = paymentAmount.amount,
                    currencyCode = paymentAmount.currencyCode,
                ),
                isPaid = false,
            ),
        )
    }

    private fun placeOrder(
        customer: UserData,
        payment: PaymentConfirmation,
    ) {
        viewModelScope.launch {
            val cartId = state.value.cart?.id ?: return@launch
            val selectedAddress = state.value.selectedAddress ?: return@launch
            _state.update { it.copy(isProcessingOrder = true) }
            setDeliveryAddressUseCase(cartId, selectedAddress.id)
                .onSuccess { cart ->
                    cart ?: run {
                        _state.update {
                            it.copy(
                                isProcessingOrder = false
                            )
                        }
                        ErrorDialogController.sendEvent(PocketDataError.Remote.EMPTY_RESULT)
                        return@onSuccess
                    }
                    placeOrderUseCase(
                        cart = cart,
                        shippingAddress = selectedAddress,
                        customer = customer,
                        payment = payment
                    )
                        .onSuccess { newOrder ->
                            restoreOrCreateCartUseCase(
                                forceRefresh = true
                            )
                            _state.update {
                                it.copy(
                                    isProcessingOrder = false,
                                    placedOrder = newOrder
                                )
                            }
                        }
                        .onError {
                            _state.update { it.copy(isProcessingOrder = false) }
                            ErrorDialogController.sendEvent(it)
                        }
                }
                .onError {
                    _state.update { it.copy(isProcessingOrder = false) }
                    ErrorDialogController.sendEvent(it)
                }

        }
    }

    private fun removeCoupon() {
        viewModelScope.launch {
            val cartId = state.value.cart?.id ?: return@launch
            _state.update { it.copy(isLoading = true) }
            removeCouponUseCase(cartId)
                .onSuccess {
                    restoreOrCreateCartUseCase()
                    _state.update {
                        it.copy(
                            isLoading = false,
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
                        )
                    }
                    restoreOrCreateCartUseCase()
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
