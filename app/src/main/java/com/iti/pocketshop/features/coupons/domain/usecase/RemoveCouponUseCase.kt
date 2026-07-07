package com.iti.pocketshop.features.coupons.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.coupons.domain.model.CartCouponResult
import com.iti.pocketshop.features.coupons.domain.repo.CouponRepository
import javax.inject.Inject

class RemoveCouponUseCase @Inject constructor(
    private val repository: CouponRepository,
) {

    suspend operator fun invoke(
        cartId: String,
    ): PocketResult<ShopifyCart?, PocketDataError> =
        repository.removeCoupons(
            cartId = cartId,
        )
}
