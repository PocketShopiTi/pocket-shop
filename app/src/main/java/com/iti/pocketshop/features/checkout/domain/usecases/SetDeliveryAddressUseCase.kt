package com.iti.pocketshop.features.checkout.domain.usecases

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.domain.repository.CheckoutRepository
import javax.inject.Inject

class SetDeliveryAddressUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository
) {

    suspend operator fun invoke(
        cartId: String,
        addressId: String
    ): PocketResult<ShopifyCart?, PocketDataError> {
        return checkoutRepository.setDeliveryAddress(
            cartId,
            addressId
        )
    }

}