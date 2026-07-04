package com.iti.pocketshop.features.ordercheckout

import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.domain.model.CheckoutAddress
import com.iti.pocketshop.features.checkout.domain.model.PaymentMethod

data class OrderCheckoutState(
    val cart: ShopifyCart? = null,
    val addresses: List<CheckoutAddress> = emptyList(),
    val selectedAddressId: String? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.ONLINE_PAYMENT,
    val couponCodeInput: String = "",
    val isLoading: Boolean = false,
    val checkoutUrl: String? = null,
    val errorMessage: String? = null
)