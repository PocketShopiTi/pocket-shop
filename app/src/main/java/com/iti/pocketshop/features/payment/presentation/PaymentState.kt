package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError

data class PaymentState(
    val isLoading: Boolean = false,
    val completedPaymentIntentId: String? = null,
    val error: PocketDataError? = null,
)
