package com.iti.pocketshop.features.payment.data.mapper

import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionResponseDto
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession

fun PaymobIntentionResponseDto.toDomain(
    publicKey: String,
): PaymobPaymentSession =
    PaymobPaymentSession(
        intentionId = id,
        clientSecret = clientSecret,
        publicKey = publicKey,
    )
