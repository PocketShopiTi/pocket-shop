package com.iti.pocketshop.features.checkout.data.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.di.AdminApolloClient
import com.iti.pocketshop.core.di.StorefrontApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.data.mappers.Order
import com.iti.pocketshop.features.checkout.data.mappers.PaymentConfirmation
import com.iti.pocketshop.features.checkout.data.mappers.toDomain
import com.iti.pocketshop.features.checkout.data.mappers.toOrderInput
import com.iti.pocketshop.features.checkout.domain.repository.CheckoutRepository
import com.iti.pocketshop.features.payment.domain.models.UserData
import com.iti.pocketshop.shopify.CartBuyerIdentityUpdateMutation
import com.iti.pocketshop.shopify.admin.PaidOrderCreateMutation
import com.iti.pocketshop.shopify.admin.type.OrderCreateOptionsInput
import com.iti.pocketshop.shopify.type.CartBuyerIdentityInput
import com.iti.pocketshop.shopify.type.DeliveryAddressInput
import javax.inject.Inject

class CheckoutRepositoryImpl @Inject constructor(
    @param:StorefrontApolloClient
    private val storeApolloClient: ApolloClient,
    @param:AdminApolloClient
    private val adminApolloClient: ApolloClient,
) : CheckoutRepository {

    override suspend fun setDeliveryAddress(
        cartId: String,
        addressId: String
    ): PocketResult<ShopifyCart?, PocketDataError> {

        val deliveryPref = DeliveryAddressInput(
            customerAddressId = Optional.present(addressId)
        )

        val identity = CartBuyerIdentityInput(
            deliveryAddressPreferences = Optional.present(listOf(deliveryPref))
        )
        return storeApolloClient.mutation(CartBuyerIdentityUpdateMutation(cartId, identity))
            .safeCall()
            .map {
                it.cartBuyerIdentityUpdate?.cart?.cartFields?.toDomain()
            }
    }

    override suspend fun placeOrder(
        cart: ShopifyCart,
        shippingAddress: Address,
        customer: UserData,
        payment: PaymentConfirmation
    ): PocketResult<Order?, PocketDataError> {
        return adminApolloClient.mutation(
            PaidOrderCreateMutation(
                order = toOrderInput(
                    cart = cart,
                    shippingAddress = shippingAddress,
                    customer = customer,
                    payment = payment
                ),
                options = Optional.present(
                    OrderCreateOptionsInput(
                        sendReceipt = Optional.present(true)
                    )
                )
            )
        )
            .safeCall()
            .map {
                it.orderCreate?.order?.toDomain()
            }
    }
}

