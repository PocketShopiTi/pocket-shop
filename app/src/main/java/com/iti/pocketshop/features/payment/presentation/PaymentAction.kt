package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData

sealed interface PaymentAction {
    data class Pay(
        val amountMinor: Long,
        val currency: PaymentCurrency,
        val userData: UserData
    ) : PaymentAction

    data class PaymobCheckoutFinished(
        val result: PocketResult<String, PocketDataError.Payment>,
    ) : PaymentAction
}
