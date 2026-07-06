package com.iti.pocketshop.features.payment.domain.repository

import  com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession

interface PaymentRepository {
    suspend fun createPaymobIntention(
        amountMinor: Long,
        currency: PaymentCurrency,
        userData: UserData,
    ): PocketResult<PaymobPaymentSession, PocketDataError.Remote>
}
