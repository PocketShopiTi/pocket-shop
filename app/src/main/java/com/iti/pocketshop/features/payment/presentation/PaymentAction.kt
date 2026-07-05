package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency

sealed interface PaymentAction {
    data class Pay(val amountMinor: Long, val currency: PaymentCurrency) : PaymentAction
    data class PaymobCheckoutFinished(
        val success: Boolean,
        val transactionId: String?,
    ) : PaymentAction

    data object DismissError : PaymentAction
}
