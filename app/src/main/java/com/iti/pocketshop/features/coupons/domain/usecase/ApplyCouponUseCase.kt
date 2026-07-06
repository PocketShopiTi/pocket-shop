package com.iti.pocketshop.features.coupons.domain.usecase

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.coupons.domain.model.CartCouponResult
import com.iti.pocketshop.features.coupons.domain.repo.CouponRepository
import javax.inject.Inject

// Takes list of coupons to apply to the cart and return cart with the  new total price
class ApplyCouponUseCase @Inject constructor(
    private val repository: CouponRepository,
) {

    suspend operator fun invoke(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<CartCouponResult, PocketDataError.Remote> =
        repository.applyCoupons(
            cartId = cartId,
            discountCodes = discountCodes,
        )
}
