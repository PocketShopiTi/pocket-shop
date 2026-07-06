package com.iti.pocketshop.features.cart.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(cartId: String, variantId: String, quantity: Int): PocketResult<ShopifyCart, PocketDataError> {
        return repository.addLines(cartId, variantId, quantity)
    }
}
