package com.iti.pocketshop.features.cart.data.mapper

import com.iti.pocketshop.features.cart.domain.model.CartCouponResult
import com.iti.pocketshop.features.cart.domain.model.CartCouponUserError
import com.iti.pocketshop.features.cart.domain.model.CartCouponWarning
import com.iti.pocketshop.features.cart.domain.model.CartCost
import com.iti.pocketshop.features.cart.domain.model.CartDiscountCode
import com.iti.pocketshop.features.cart.domain.model.CartMoney
import com.iti.pocketshop.features.cart.domain.model.CouponCart
import com.iti.pocketshop.shopify.ApplyDiscountCodeMutation.CartDiscountCodesUpdate

fun CartDiscountCodesUpdate.toDomain(): CartCouponResult = CartCouponResult(
    cart = cart?.let { cart ->
        CouponCart(
            id = cart.id,
            discountCodes = cart.discountCodes.map { discountCode ->
                CartDiscountCode(
                    code = discountCode.code,
                    applicable = discountCode.applicable,
                )
            },
            cost = CartCost(
                subtotalAmount = CartMoney(
                    amount = cart.cost.subtotalAmount.amount,
                    currencyCode = cart.cost.subtotalAmount.currencyCode.name,
                ),
                totalAmount = CartMoney(
                    amount = cart.cost.totalAmount.amount,
                    currencyCode = cart.cost.totalAmount.currencyCode.name,
                ),
            ),
        )
    },
    userErrors = userErrors.map { userError ->
        CartCouponUserError(
            code = userError.code?.rawValue,
            fields = userError.field,
            message = userError.message,
        )
    },
    warnings = warnings.map { warning ->
        CartCouponWarning(
            code = warning.code.rawValue,
            message = warning.message,
            target = warning.target,
        )
    },
)
