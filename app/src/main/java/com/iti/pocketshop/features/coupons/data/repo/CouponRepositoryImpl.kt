package com.iti.pocketshop.features.coupons.data.repo

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.coupons.data.datasource.CouponDataSource
import com.iti.pocketshop.features.coupons.data.mapper.toDomain
import com.iti.pocketshop.features.coupons.domain.model.CartCouponResult
import com.iti.pocketshop.features.coupons.domain.repo.CouponRepository
import javax.inject.Inject

class CouponRepositoryImpl @Inject constructor(
    private val dataSource: CouponDataSource,
) : CouponRepository {

    override suspend fun applyCoupons(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<CartCouponResult, PocketDataError> {
        val result = dataSource.applyCoupons(
            cartId = cartId,
            discountCodes = discountCodes,
        ).map { payload -> payload.toDomain() }

        if (result is PocketResult.Success) {
            if (result.data.warnings.isNotEmpty()) {
                return PocketResult.Error(PocketDataError.Remote.INVALID_COUPON)
            }
        }
        return result
    }

    override suspend fun removeCoupons(
        cartId: String,
    ): PocketResult<ShopifyCart?, PocketDataError> {
        return dataSource.removeCoupons(
            cartId = cartId,
        )
    }
}
