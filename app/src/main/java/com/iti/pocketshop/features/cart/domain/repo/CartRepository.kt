package com.iti.pocketshop.features.cart.domain.repo

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.model.CartCouponResult

interface CartRepository {
    suspend fun applyCoupons(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<CartCouponResult, PocketDataError.Remote>
}
