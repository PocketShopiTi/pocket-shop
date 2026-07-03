package com.iti.pocketshop.features.cart.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.model.CartCouponResult
import com.iti.pocketshop.features.cart.domain.repo.CartRepository
import javax.inject.Inject

// Takes list of coupons to apply to the cart and return cart with the  new total price
class ApplyCouponUseCase @Inject constructor(
    private val repository: CartRepository,
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
