package com.iti.pocketshop.features.payment.data.remote

import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeRestCall
import com.iti.pocketshop.features.payment.data.dto.PaymentIntentDto
import com.iti.pocketshop.features.payment.di.PaymentHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import javax.inject.Inject

interface StripePaymentDataSource {
    suspend fun createPaymentIntent(
        amountMinor: Long,
        currencyCode: String,
    ): PocketResult<PaymentIntentDto, PocketDataError.Remote>
}

class StripePaymentDataSourceImpl @Inject constructor(
    @PaymentHttpClient private val client: HttpClient,
) : StripePaymentDataSource {

    private companion object {
        const val PAYMENT_INTENTS_URL = "https://api.stripe.com/v1/payment_intents"
    }

    override suspend fun createPaymentIntent(
        amountMinor: Long,
        currencyCode: String,
    ): PocketResult<PaymentIntentDto, PocketDataError.Remote> = safeRestCall {
        client.post(PAYMENT_INTENTS_URL) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${BuildConfig.STRIPE_SECRET_KEY}"
            )
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("amount", amountMinor.toString())
                        append("currency", currencyCode)
                        append("automatic_payment_methods[enabled]", "true")
                    }
                )
            )
        }.body<PaymentIntentDto>()
    }
}
