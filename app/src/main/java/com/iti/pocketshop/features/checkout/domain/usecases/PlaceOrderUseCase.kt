package com.iti.pocketshop.features.checkout.domain.usecases

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.data.mappers.Order
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation
import com.iti.pocketshop.features.checkout.domain.repository.CheckoutRepository
import com.iti.pocketshop.features.payment.domain.models.UserData
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val repository: CheckoutRepository,
) {

    suspend operator fun invoke(
        cart: ShopifyCart,
        shippingAddress: Address,
        customer: UserData,
        payment: PaymentConfirmation
    ): PocketResult<Order?, PocketDataError> {
        return repository.placeOrder(
            cart = cart,
            shippingAddress = shippingAddress,
            customer = customer,
            payment = payment
        )
    }

}