package com.iti.pocketshop.features.payment.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.PaymentIntentSession
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class CreatePaymentIntentUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(
        amountMinor: Long,
        currency: PaymentCurrency,
    ): PocketResult<PaymentIntentSession, PocketDataError.Remote> =
        repository.createPaymentIntent(amountMinor, currency)
}
