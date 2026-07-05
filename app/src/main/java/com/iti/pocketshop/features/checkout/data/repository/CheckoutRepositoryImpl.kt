package com.iti.pocketshop.features.checkout.data.repository

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.features.checkout.domain.model.CheckoutAddress
import com.iti.pocketshop.features.checkout.domain.repository.CheckoutRepository
import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.shopify.CartBuyerIdentityUpdateMutation
import com.iti.pocketshop.shopify.CartDiscountCodesUpdateMutation
import com.iti.pocketshop.shopify.GetCartQuery
import com.iti.pocketshop.shopify.GetCustomerAddressesQuery
import com.iti.pocketshop.shopify.type.CartBuyerIdentityInput
import com.iti.pocketshop.shopify.type.DeliveryAddressInput
import com.iti.pocketshop.shopify.type.MailingAddressInput
import javax.inject.Inject

class CheckoutRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val tokenRepository: CustomerAccessTokenRepository
) : CheckoutRepository {

    override suspend fun getCustomerAddresses(): Result<List<CheckoutAddress>> {
        val resultToken = tokenRepository.getValidToken()
        val token = if (resultToken is PocketResult.Success) resultToken.data.accessToken else return Result.failure(Exception("No valid token"))
        val result = apolloClient.query(GetCustomerAddressesQuery(token)).safeCall()
        
        return when(result) {
            is PocketResult.Success -> {
                val addresses = result.data.customer?.addresses?.nodes?.map { edge ->
                    CheckoutAddress(
                        id = edge.id,
                        firstName = edge.firstName ?: "",
                        lastName = edge.lastName ?: "",
                        address1 = edge.address1 ?: "",
                        address2 = edge.address2 ?: "",
                        city = edge.city ?: "",
                        country = edge.country ?: "",
                        phone = edge.phone ?: ""
                    )
                } ?: emptyList()
                Result.success(addresses)
            }
            is PocketResult.Error -> Result.failure(Exception(result.error.toString()))
        }
    }

    override suspend fun applyCoupon(cartId: String, couponCode: String): Result<ShopifyCart> {
        val result = apolloClient.mutation(CartDiscountCodesUpdateMutation(cartId, listOf(couponCode))).safeCall()
        return when(result) {
            is PocketResult.Success -> {
                val cart = result.data.cartDiscountCodesUpdate?.cart?.cartFields?.toDomain()
                if (cart != null) Result.success(cart) else Result.failure(Exception("Failed to apply coupon"))
            }
            is PocketResult.Error -> Result.failure(Exception(result.error.toString()))
        }
    }

    override suspend fun removeCoupon(cartId: String): Result<ShopifyCart> {
        // Shopify API expects an empty array to remove all discount codes
        val result = apolloClient.mutation(CartDiscountCodesUpdateMutation(cartId, emptyList())).safeCall()
        return when(result) {
            is PocketResult.Success -> {
                val cart = result.data.cartDiscountCodesUpdate?.cart?.cartFields?.toDomain()
                if (cart != null) Result.success(cart) else Result.failure(Exception("Failed to remove coupon"))
            }
            is PocketResult.Error -> Result.failure(Exception(result.error.toString()))
        }
    }

    override suspend fun setDeliveryAddress(cartId: String, addressId: String): Result<ShopifyCart> {
        // Note: For Shopify Storefront API Cart, we need to pass the deliveryAddressPreferences.
        // Wait, the CartBuyerIdentityInput allows setting customerAddressId
        // Let's check the schema to see if deliveryAddressPreferences takes a String or a list of CartDeliveryAddressPreferencesInput.
        // According to standard Shopify API, it's: deliveryAddressPreferences: [{customerAddressId: "id"}]
        
        val deliveryPref = DeliveryAddressInput(
            customerAddressId = com.apollographql.apollo.api.Optional.present(addressId)
        )
        
        val identity = CartBuyerIdentityInput(
            deliveryAddressPreferences = com.apollographql.apollo.api.Optional.present(listOf(deliveryPref))
        )
        val result = apolloClient.mutation(CartBuyerIdentityUpdateMutation(cartId, identity)).safeCall()
        
        return when(result) {
            is PocketResult.Success -> {
                val cart = result.data.cartBuyerIdentityUpdate?.cart?.cartFields?.toDomain()
                if (cart != null) Result.success(cart) else Result.failure(Exception("Failed to set address"))
            }
            is PocketResult.Error -> Result.failure(Exception(result.error.toString()))
        }
    }

    override suspend fun getCheckoutUrl(cartId: String): Result<String> {
        val result = apolloClient.query(GetCartQuery(cartId)).safeCall()
        return when(result) {
            is PocketResult.Success -> {
                val checkoutUrl = result.data.cart?.cartFields?.checkoutUrl?.toString()
                if (checkoutUrl != null) Result.success(checkoutUrl) else Result.failure(Exception("No URL"))
            }
            is PocketResult.Error -> Result.failure(Exception(result.error.toString()))
        }
    }

    override suspend fun createTestAddress(): Result<Unit> {
        val resultToken = tokenRepository.getValidToken()
        val token = if (resultToken is PocketResult.Success) resultToken.data.accessToken else return Result.failure(Exception("No valid token"))
        
        val addressInput = MailingAddressInput(
            address1 = com.apollographql.apollo.api.Optional.present("123 Test Street"),
            city = com.apollographql.apollo.api.Optional.present("Test City"),
            country = com.apollographql.apollo.api.Optional.present("United States"),
            firstName = com.apollographql.apollo.api.Optional.present("Test"),
            lastName = com.apollographql.apollo.api.Optional.present("User"),
            phone = com.apollographql.apollo.api.Optional.present("1234567890"),
            zip = com.apollographql.apollo.api.Optional.present("12345")
        )
        
        val result = apolloClient.mutation(com.iti.pocketshop.shopify.CustomerAddressCreateMutation(token, addressInput)).safeCall()
        return when (result) {
            is PocketResult.Success -> {
                if (result.data.customerAddressCreate?.userErrors?.isEmpty() == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(result.data.customerAddressCreate?.userErrors?.firstOrNull()?.message))
                }
            }
            is PocketResult.Error -> Result.failure(Exception(result.error.toString()))
        }
    }
}
