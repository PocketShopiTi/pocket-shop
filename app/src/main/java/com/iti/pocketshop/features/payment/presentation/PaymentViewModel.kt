package com.iti.pocketshop.features.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
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
            is PaymentAction.Pay -> payWithPaymob(action.amountMinor, action.currency)
            is PaymentAction.PaymobCheckoutFinished ->
                onCheckoutFinished(action.success, action.transactionId)

            PaymentAction.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun payWithPaymob(amountMinor: Long, currency: PaymentCurrency) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, error = null, completedPaymentId = null)
            }
            createPaymobPayment(amountMinor, currency)
                .onSuccess { session ->
                    _state.update { it.copy(isLoading = false, paymobCheckout = session) }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false, error = error) }
                }
        }
    }

    private fun onCheckoutFinished(success: Boolean, transactionId: String?) {
        val intentionId = _state.value.paymobCheckout?.intentionId
        _state.update {
            if (success) {
                it.copy(
                    paymobCheckout = null,
                    completedPaymentId = transactionId ?: intentionId,
                )
            } else {
                it.copy(paymobCheckout = null, error = PocketDataError.Payment.FAILED)
            }
        }
    }
}
