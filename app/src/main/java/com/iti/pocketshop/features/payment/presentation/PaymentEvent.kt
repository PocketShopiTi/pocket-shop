package com.iti.pocketshop.features.payment.presentation

import com.iti.pocketshop.features.payment.domain.models.PaymentIntentSession

sealed interface PaymentEvent {
    data class LaunchSheet(val session: PaymentIntentSession) : PaymentEvent
}
