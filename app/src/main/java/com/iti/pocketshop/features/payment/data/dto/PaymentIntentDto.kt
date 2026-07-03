package com.iti.pocketshop.features.payment.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntentDto(
    @SerialName("id") val id: String,
    @SerialName("client_secret") val clientSecret: String,
)
