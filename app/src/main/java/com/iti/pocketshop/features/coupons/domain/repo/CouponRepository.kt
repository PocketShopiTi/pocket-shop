package com.iti.pocketshop.features.coupons.domain.repo

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.coupons.domain.model.CartCouponResult

interface CouponRepository {
    suspend fun applyCoupons(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<CartCouponResult, PocketDataError>

    suspend fun removeCoupons(
        cartId: String,
    ): PocketResult<ShopifyCart?, PocketDataError>
}
