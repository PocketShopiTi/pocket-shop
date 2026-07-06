package com.iti.pocketshop.features.checkout.presentation

import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.data.mappers.Order
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation

data class CheckoutState(
    val isLoading: Boolean = false,
    val cart: ShopifyCart? = null,
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val couponCodeInput: String = "",
    val appliedCouponCode: String = "",
    val paymentConfirmation: PaymentConfirmation? = null,
    val placedOrder: Order? = null,
)