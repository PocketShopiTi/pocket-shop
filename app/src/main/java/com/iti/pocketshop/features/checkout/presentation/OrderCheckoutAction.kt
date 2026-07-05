package com.iti.pocketshop.features.checkout.presentation

import com.iti.pocketshop.features.checkout.domain.model.PaymentMethod

sealed interface OrderCheckoutAction {
    data class OnCouponInputChanged(val code: String) : OrderCheckoutAction
    data object ApplyCoupon : OrderCheckoutAction
    data class RemoveCoupon(val code: String) : OrderCheckoutAction
    data class SelectAddress(val addressId: String) : OrderCheckoutAction
    data class SelectPaymentMethod(val method: PaymentMethod) : OrderCheckoutAction
    data object PlaceOrder : OrderCheckoutAction
    data object AddTestAddress : OrderCheckoutAction
    data object CheckoutHandled : OrderCheckoutAction
    data object ErrorHandled : OrderCheckoutAction
}