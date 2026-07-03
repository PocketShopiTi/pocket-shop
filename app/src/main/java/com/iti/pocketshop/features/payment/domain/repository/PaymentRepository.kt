package com.iti.pocketshop.features.payment.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.PaymentIntentSession

interface PaymentRepository {
    suspend fun createPaymentIntent(
        amountMinor: Long,
        currency: PaymentCurrency,
    ): PocketResult<PaymentIntentSession, PocketDataError.Remote>
}
