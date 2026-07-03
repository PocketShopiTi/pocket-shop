package com.iti.pocketshop.features.payment.domain.models

data class PaymentIntentSession(
    val paymentIntentId: String,
    val clientSecret: String,
    val publishableKey: String,
)
