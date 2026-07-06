package com.iti.pocketshop.features.payment.domain.datasource

import com.iti.pocketshop.network.PocketDataError
import  com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.payment.data.dto.PaymobIntentionResponseDto
import com.iti.pocketshop.features.payment.domain.models.UserData


interface PaymobPaymentDataSource {
    suspend fun createIntention(
        amountMinor: Long,
        currencyCode: String,
        userData: UserData,
    ): PocketResult<PaymobIntentionResponseDto, PocketDataError.Remote>
}