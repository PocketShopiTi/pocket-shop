package com.iti.pocketshop.features.payment.data.repository

import com.iti.pocketshop.BuildConfig

import com.iti.pocketshop.features.payment.data.mapper.toDomain
import com.iti.pocketshop.features.payment.domain.datasource.PaymobPaymentDataSource
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import com.iti.pocketshop.network.PocketResult
import javax.inject.Inject
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.map

class PaymentRepositoryImpl @Inject constructor(
    private val remote: PaymobPaymentDataSource,
) : PaymentRepository {

    override suspend fun createPaymobIntention(
        amountMinor: Long,
        currency: PaymentCurrency,
        userData: UserData,
    ): PocketResult<PaymobPaymentSession, PocketDataError.Remote> =
        remote.createIntention(amountMinor, currency.currencyCode, userData)

            .map { dto ->

                dto.toDomain(
                    publicKey = BuildConfig.PAYMOB_PUBLIC_KEY,
                )
            }
}
