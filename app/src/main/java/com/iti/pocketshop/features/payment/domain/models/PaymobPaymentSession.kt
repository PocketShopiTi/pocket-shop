package com.iti.pocketshop.features.payment.domain.models

data class PaymobPaymentSession(
    val intentionId: String,
    val clientSecret: String,
    val checkoutUrl: String,
    val redirectUrl: String,
)
