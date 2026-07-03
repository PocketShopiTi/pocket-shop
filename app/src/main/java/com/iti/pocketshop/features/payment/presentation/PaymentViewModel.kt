package com.iti.pocketshop.features.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.usecase.CreatePaymentIntentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createPaymentIntent: CreatePaymentIntentUseCase,
) : ViewModel() {

    private var pendingPaymentIntentId: String? = null

    private val _state = MutableStateFlow(PaymentState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PaymentState()
        )

    private val _events = Channel<PaymentEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: PaymentAction) {
        when (action) {
            is PaymentAction.Pay -> pay(action.amountMinor, action.currency)
            PaymentAction.SheetCompleted -> onSheetCompleted()
            PaymentAction.SheetCanceled -> onSheetClosed(PocketDataError.Payment.CANCELED)
            PaymentAction.SheetFailed -> onSheetClosed(PocketDataError.Payment.FAILED)
            PaymentAction.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun pay(amountMinor: Long, currency: PaymentCurrency) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, error = null, completedPaymentIntentId = null)
            }
            createPaymentIntent(amountMinor, currency)
                .onSuccess { session ->
                    pendingPaymentIntentId = session.paymentIntentId
                    _state.update { it.copy(isLoading = false) }
                    _events.send(PaymentEvent.LaunchSheet(session))
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false, error = error) }
                }
        }
    }

    private fun onSheetCompleted() {
        _state.update { it.copy(completedPaymentIntentId = pendingPaymentIntentId) }
        pendingPaymentIntentId = null
    }

    private fun onSheetClosed(error: PocketDataError.Payment) {
        pendingPaymentIntentId = null
        _state.update { it.copy(error = error) }
    }
}
