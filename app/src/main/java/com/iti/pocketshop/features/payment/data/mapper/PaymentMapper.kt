package com.iti.pocketshop.features.payment.data.mapper

import com.iti.pocketshop.features.payment.data.dto.PaymentIntentDto
import com.iti.pocketshop.features.payment.domain.models.PaymentIntentSession

fun PaymentIntentDto.toDomain(publishableKey: String): PaymentIntentSession =
    PaymentIntentSession(
        paymentIntentId = id,
        clientSecret = clientSecret,
        publishableKey = publishableKey,
    )
