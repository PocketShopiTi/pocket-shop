package com.iti.pocketshop.features.payment.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class CreatePaymobPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(
        amountMinor: Long,
        currency: PaymentCurrency,
        userData: UserData,
    ): PocketResult<PaymobPaymentSession, PocketDataError.Remote> =
        repository.createPaymobIntention(amountMinor, currency, userData)
}
