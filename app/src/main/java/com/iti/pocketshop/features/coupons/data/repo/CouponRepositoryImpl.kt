package com.iti.pocketshop.features.coupons.data.repo

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
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
    ): PocketResult<CartCouponResult, PocketDataError.Remote> {
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
}
