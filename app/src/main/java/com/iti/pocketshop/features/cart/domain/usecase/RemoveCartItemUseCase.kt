package com.iti.pocketshop.features.cart.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(cartId: String, lineId: String): PocketResult<ShopifyCart, PocketDataError> {
        return repository.removeLines(cartId, listOf(lineId))
    }
}
