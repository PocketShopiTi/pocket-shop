package com.iti.pocketshop.features.payment.data.mapper

import com.iti.pocketshop.features.payment.data.dto.PaymobBillingDataDto
import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionResponseDto
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession

fun PaymobIntentionResponseDto.toDomain(
    publicKey: String,
): PaymobPaymentSession {
    return PaymobPaymentSession(
        intentionId = id,
        clientSecret = clientSecret,
        publicKey = publicKey,
    )
}

fun UserData.toDto(): PaymobBillingDataDto {
    return PaymobBillingDataDto(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
    )
}