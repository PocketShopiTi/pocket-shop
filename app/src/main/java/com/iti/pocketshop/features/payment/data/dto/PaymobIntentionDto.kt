package com.iti.pocketshop.features.payment.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymobIntentionRequestDto(
    @SerialName("amount") val amount: Long,
    @SerialName("currency") val currency: String,
    @SerialName("payment_methods") val paymentMethods: List<Int>,
    @SerialName("items") val items: List<PaymobItemDto> = emptyList(),
    @SerialName("billing_data") val billingData: PaymobBillingDataDto,
    @SerialName("redirection_url") val redirectionUrl: String,
)


@Serializable
data class PaymobItemDto(
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Long,
    @SerialName("description") val description: String,
    @SerialName("quantity") val quantity: Int,
)

@Serializable
data class PaymobBillingDataDto(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("apartment") val apartment: String = "NA",
    @SerialName("floor") val floor: String = "NA",
    @SerialName("street") val street: String = "NA",
    @SerialName("building") val building: String = "NA",
    @SerialName("shipping_method") val shippingMethod: String = "NA",
    @SerialName("postal_code") val postalCode: String = "NA",
    @SerialName("city") val city: String = "NA",
    @SerialName("country") val country: String = "EG",
    @SerialName("state") val state: String = "NA",
)

@Serializable
data class PaymobIntentionResponseDto(
    @SerialName("id") val id: String,
    @SerialName("client_secret") val clientSecret: String,
    @SerialName("payment_methods")
    val paymentMethods: List<PaymentMethod> = emptyList(),
)

@Serializable
data class PaymentMethod(
    @SerialName("integration_id")
    val integrationId: Int,

    val alias: String? = null,
    val name: String,

    @SerialName("method_type")
    val methodType: String,

    val currency: String,
    val live: Boolean,

    @SerialName("use_cvc_with_moto")
    val useCvcWithMoto: Boolean
)