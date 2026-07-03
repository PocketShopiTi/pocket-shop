package com.iti.pocketshop.features.cart.domain.model

data class CartCouponResult(
    val cart: CouponCart?,
    val userErrors: List<CartCouponUserError>,
    val warnings: List<CartCouponWarning>,
)

data class CouponCart(
    val id: String,
    val discountCodes: List<CartDiscountCode>,
    val cost: CartCost,
)

data class CartDiscountCode(
    val code: String,
    val applicable: Boolean,
)

data class CartCost(
    val subtotalAmount: CartMoney,
    val totalAmount: CartMoney,
)

data class CartMoney(
    val amount: Double,
    val currencyCode: String,
)

data class CartCouponUserError(
    val code: String?,
    val fields: List<String>?,
    val message: String,
)

data class CartCouponWarning(
    val code: String,
    val message: String,
    val target: String,
)
