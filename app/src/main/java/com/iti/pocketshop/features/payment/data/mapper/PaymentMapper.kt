package com.iti.pocketshop.features.payment.data.mapper

import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionResponseDto
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession

private const val UNIFIED_CHECKOUT_URL = "https://accept.paymob.com/unifiedcheckout/"

fun PaymobIntentionResponseDto.toDomain(
    publicKey: String,
    redirectUrl: String,
): PaymobPaymentSession =
    PaymobPaymentSession(
        intentionId = id,
        clientSecret = clientSecret,
        checkoutUrl = "$UNIFIED_CHECKOUT_URL?publicKey=$publicKey&clientSecret=$clientSecret",
        redirectUrl = redirectUrl,
    )
