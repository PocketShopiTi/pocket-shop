package com.iti.pocketshop.features.checkout.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.data.mappers.Order
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation
import com.iti.pocketshop.features.payment.domain.models.UserData

interface CheckoutRepository {
    
    suspend fun setDeliveryAddress(
        cartId: String,
        addressId: String
    ): PocketResult<ShopifyCart?, PocketDataError>

    suspend fun placeOrder(
        cart: ShopifyCart,
        shippingAddress: Address,
        customer: UserData,
        payment: PaymentConfirmation
    ): PocketResult<Order?, PocketDataError>
}
