package com.iti.pocketshop.features.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import  com.iti.pocketshop.network.PocketDataError
import  com.iti.pocketshop.network.onError
import com.iti.pocketshop.network.onSuccess
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.usecase.CreatePaymobPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.asStateFlow
 import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createPaymobPayment: CreatePaymobPaymentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    fun onAction(action: PaymentAction) {
        when (action) {
            is PaymentAction.Pay -> payWithPaymob(
                action.amountMinor,
                action.currency,
                action.userData
            )

            is PaymentAction.OnPaymobSuccess -> handlePaymobSuccess(action.response)
            is PaymentAction.OnPaymobFailure -> handlePaymobFailure(action.message)
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

    private fun handlePaymobSuccess(response: HashMap<String, String?>) {
        val transactionId = response["id"] ?: _state.value.paymobCheckout?.intentionId ?: "unknown"
        _state.update {
            it.copy(
                paymobCheckout = null,
                completedPaymentId = transactionId,
                isLoading = false
            )
        }
    }

    private fun handlePaymobFailure(msg: String?) {
        val error = mapPaymobError(msg)
        viewModelScope.launch {
            ErrorDialogController.sendEvent(error)
            _state.update {
                it.copy(
                    paymobCheckout = null,
                    isLoading = false
                )
            }
        }
    }

    private fun mapPaymobError(msg: String?): PocketDataError.Payment {
        return when {
            msg.isNullOrBlank() ||
                    msg.contains("null", ignoreCase = true) ||
                    msg.contains("cancel", ignoreCase = true) ->
                PocketDataError.Payment.CANCELED

            msg.contains("funds", ignoreCase = true) ->
                PocketDataError.Payment.NO_FUNDS

            msg.contains("declined", ignoreCase = true) ||
                    msg.contains("rejected", ignoreCase = true) ||
                    msg.contains("auth", ignoreCase = true) ||
                    msg.contains("secure", ignoreCase = true) ->
                PocketDataError.Payment.REJECTED

            msg.contains("expired", ignoreCase = true) ->
                PocketDataError.Payment.EXPIRED

            msg.contains("invalid", ignoreCase = true) ||
                    msg.contains("card", ignoreCase = true) ||
                    msg.contains("cvv", ignoreCase = true) ||
                    msg.contains("number", ignoreCase = true) ->
                PocketDataError.Payment.INVALID_CARD

            else -> PocketDataError.Payment.FAILED
        }
    }
}
