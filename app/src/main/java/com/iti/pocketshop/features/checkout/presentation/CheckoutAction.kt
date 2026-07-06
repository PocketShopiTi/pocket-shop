package com.iti.pocketshop.features.checkout.presentation

import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation
import com.iti.pocketshop.features.payment.domain.models.UserData


sealed interface CheckoutAction {
    data class OnCouponInputChanged(val code: String) : CheckoutAction
    data object ApplyCoupon : CheckoutAction
    data class RemoveCoupon(val code: String) : CheckoutAction
    data class SelectAddress(val address: Address) : CheckoutAction
    data class OnPaymentSuccess(
        val customer: UserData,
        val paymentConfirmation: PaymentConfirmation
    ) : CheckoutAction
}