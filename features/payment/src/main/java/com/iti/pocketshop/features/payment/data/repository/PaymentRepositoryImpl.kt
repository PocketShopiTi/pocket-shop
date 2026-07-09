package com.iti.pocketshop.features.payment.data.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.payment.data.mapper.toDomain
import com.iti.pocketshop.features.payment.domain.config.PaymobConfig
import com.iti.pocketshop.features.payment.domain.datasource.PaymobPaymentDataSource
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.PaymobPaymentSession
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remote: PaymobPaymentDataSource,
    private val paymobConfig: PaymobConfig,
) : PaymentRepository {

    override suspend fun createPaymobIntention(
        amountMinor: Long,
        currency: PaymentCurrency,
        userData: UserData,
    ): PocketResult<PaymobPaymentSession, PocketDataError.Remote> =
        remote.createIntention(amountMinor, currency.currencyCode, userData)
            .map { dto ->
                dto.toDomain(
                    publicKey = paymobConfig.publicKey,
                )
            }
}
