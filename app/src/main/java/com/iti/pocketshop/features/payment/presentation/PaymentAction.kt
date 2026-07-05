package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.features.payment.domain.models.PaymentCurrency
import com.iti.pocketshop.features.payment.domain.models.UserData

sealed interface PaymentAction {
    data class Pay(
        val amountMinor: Long,
        val currency: PaymentCurrency,
        val userData: UserData
    ) : PaymentAction

    data class OnPaymobSuccess(val response: HashMap<String, String?>) : PaymentAction
    data class OnPaymobFailure(val message: String?) : PaymentAction
}
