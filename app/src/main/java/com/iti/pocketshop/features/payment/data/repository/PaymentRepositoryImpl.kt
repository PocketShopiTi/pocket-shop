package com.iti.pocketshop.features.payment.data.repository

import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.payment.data.mapper.toDomain
import com.iti.pocketshop.features.payment.data.remote.StripePaymentDataSource
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.PaymentIntentSession
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remote: StripePaymentDataSource,
) : PaymentRepository {

    override suspend fun createPaymentIntent(
        amountMinor: Long,
        currency: PaymentCurrency,
    ): PocketResult<PaymentIntentSession, PocketDataError.Remote> =
        remote.createPaymentIntent(amountMinor, currency.currencyCode)
            .map { dto -> dto.toDomain(publishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY) }
}
