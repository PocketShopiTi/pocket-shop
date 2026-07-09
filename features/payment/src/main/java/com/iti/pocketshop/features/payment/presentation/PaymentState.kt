package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession

data class PaymentState(
    val isLoading: Boolean = false,
    val paymobCheckout: PaymobPaymentSession? = null,
    val completedPaymentId: String? = null,
)
