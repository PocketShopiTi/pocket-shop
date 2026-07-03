package com.iti.pocketshop.features.payment.data.repository

import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.payment.data.mapper.toDomain
import com.iti.pocketshop.features.payment.data.remote.PaymobPaymentDataSource
import com.iti.pocketshop.features.payment.data.remote.PaymobPaymentDataSourceImpl
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remote: PaymobPaymentDataSource,
) : PaymentRepository {

    override suspend fun createPaymobIntention(
        amountMinor: Long,
        currency: PaymentCurrency,
    ): PocketResult<PaymobPaymentSession, PocketDataError.Remote> =
        remote.createIntention(amountMinor, currency.currencyCode)
            .map { dto ->
                dto.toDomain(
                    publicKey = BuildConfig.PAYMOB_PUBLIC_KEY,
                    redirectUrl = PaymobPaymentDataSourceImpl.REDIRECT_URL,
                )
            }
}
