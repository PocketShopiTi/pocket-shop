package com.iti.pocketshop.features.payment.domain.models

enum class PaymentCurrency(val currencyCode: String) {
    EGP("EGP"),
    USD("USD"),
}

enum class PaymentGateway(
    val gatewayName: String,
) {
    PayMob(
        gatewayName = "PayMob",
    )
}