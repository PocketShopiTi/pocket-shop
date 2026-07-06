package com.iti.pocketshop.features.cart.domain.usecase

import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<ShopifyCart?> {
        return repository.getLocalCart()
    }
}
