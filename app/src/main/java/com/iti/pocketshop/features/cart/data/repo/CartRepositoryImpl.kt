package com.iti.pocketshop.features.cart.data.repo

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.cart.data.datasource.CartDataSource
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.model.CartCouponResult
import com.iti.pocketshop.features.cart.domain.repo.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val dataSource: CartDataSource,
) : CartRepository {

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
