package com.iti.pocketshop.features.cart

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.features.coupons.domain.model.CartCouponResult

data class CartState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
    val cartId: String = "",
    val couponCode: String = "",
    val isApplyingCoupon: Boolean = false,
    val couponResult: CartCouponResult? = null,
    val error: PocketDataError.Remote? = null,
) {
    val canApplyCoupon: Boolean
        get() = cartId.isNotBlank() && couponCode.isNotBlank() && !isApplyingCoupon
}
