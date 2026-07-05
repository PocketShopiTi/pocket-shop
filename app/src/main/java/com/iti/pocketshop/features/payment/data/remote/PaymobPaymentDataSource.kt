package com.iti.pocketshop.features.payment.data.remote

import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeRestCall
import com.iti.pocketshop.features.payment.data.dto.PaymobBillingDataDto
import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionRequestDto
import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionResponseDto
import com.iti.pocketshop.features.payment.di.PaymentHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import javax.inject.Inject

interface PaymobPaymentDataSource {
    suspend fun createIntention(
        amountMinor: Long,
        currencyCode: String,
    ): PocketResult<PaymobIntentionResponseDto, PocketDataError.Remote>
}

class PaymobPaymentDataSourceImpl @Inject constructor(
    @param:PaymentHttpClient private val client: HttpClient,
) : PaymobPaymentDataSource {

    companion object {
        private const val INTENTION_URL = "https://accept.paymob.com/v1/intention/"

        // Placeholder billing data until the real customer/checkout data is wired in.
        // Paymob requires every field; "NA" is its documented placeholder value.
        private val PLACEHOLDER_BILLING_DATA = PaymobBillingDataDto(
            firstName = "NA",
            lastName = "NA",
            email = "guest@pocketshop.app",
            phoneNumber = "+201000000000",
        )
    }

    override suspend fun createIntention(
        amountMinor: Long,
        currencyCode: String,
    ): PocketResult<PaymobIntentionResponseDto, PocketDataError.Remote> = safeRestCall {
        client.post(INTENTION_URL) {
            header(HttpHeaders.Authorization, "Token ${BuildConfig.PAYMOB_SECRET_KEY}")
            contentType(ContentType.Application.Json)
            setBody(
                PaymobIntentionRequestDto(
                    amount = amountMinor,
                    currency = currencyCode,
                    paymentMethods = listOf(
                        BuildConfig.CARD_PAYMOB_INTEGRATION_ID.toInt(),
                        BuildConfig.WALLET_PAYMOB_INTEGRATION_ID.toInt(),
                        BuildConfig.KIOSK_PAYMOB_INTEGRATION_ID.toInt(),
                    ),
                    billingData = PLACEHOLDER_BILLING_DATA,
                    redirectionUrl = "https://pocketshop.app/payment/complete",
                )
            )
        }.body<PaymobIntentionResponseDto>()
    }
}
