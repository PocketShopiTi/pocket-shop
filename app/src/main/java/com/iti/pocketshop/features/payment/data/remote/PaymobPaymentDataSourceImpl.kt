package com.iti.pocketshop.features.payment.data.remote

import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeRestCall
import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionRequestDto
import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionResponseDto
import com.iti.pocketshop.features.payment.data.mapper.toDto
import com.iti.pocketshop.features.payment.domain.datasource.PaymobPaymentDataSource
import com.iti.pocketshop.features.payment.domain.models.UserData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import javax.inject.Inject


class PaymobPaymentDataSourceImpl @Inject constructor(
    private val client: HttpClient,
) : PaymobPaymentDataSource {

    private val intentionUrl = "https://accept.paymob.com/v1/intention/"
    private val redirectionUrl = "https://pocketshop.app/payment/complete"

    override suspend fun createIntention(
        amountMinor: Long,
        currencyCode: String,
        userData: UserData,
    ): PocketResult<PaymobIntentionResponseDto, PocketDataError.Remote> = safeRestCall {
        client.post(intentionUrl) {
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
                    billingData = userData.toDto(),
                    redirectionUrl = redirectionUrl,
                )
            )
        }.body<PaymobIntentionResponseDto>()
    }
}
