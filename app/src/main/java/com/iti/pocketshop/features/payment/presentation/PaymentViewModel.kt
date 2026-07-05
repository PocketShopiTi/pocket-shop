package com.iti.pocketshop.features.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.usecase.CreatePaymobPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createPaymobPayment: CreatePaymobPaymentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PaymentState()
        )

    fun onAction(action: PaymentAction) {
        when (action) {
            is PaymentAction.Pay -> payWithPaymob(
                action.amountMinor,
                action.currency,
                action.userData
            )

            is PaymentAction.PaymobCheckoutFinished ->
                onCheckoutFinished(action.result)
        }
    }

    private fun payWithPaymob(
        amountMinor: Long,
        currency: PaymentCurrency,
        userData: UserData
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, completedPaymentId = null)
            }
            createPaymobPayment(amountMinor, currency, userData)
                .onSuccess { session ->
                    _state.update { it.copy(paymobCheckout = session) }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun onCheckoutFinished(result: PocketResult<String, PocketDataError.Payment>) {
        viewModelScope.launch {
            result
                .onSuccess { data ->
                    _state.update { currentState ->
                        currentState.copy(
                            paymobCheckout = null,
                            completedPaymentId = data,
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    ErrorDialogController.sendEvent(error)
                    _state.update { currentState ->
                        currentState.copy(
                            paymobCheckout = null,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
