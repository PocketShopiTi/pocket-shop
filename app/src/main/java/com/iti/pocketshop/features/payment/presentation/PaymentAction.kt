package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency

sealed interface PaymentAction {
    data class Pay(val amountMinor: Long, val currency: PaymentCurrency) : PaymentAction
    data object SheetCompleted : PaymentAction
    data object SheetCanceled : PaymentAction
    data object SheetFailed : PaymentAction
    data object DismissError : PaymentAction
}
