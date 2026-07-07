package com.iti.pocketshop.features.cart.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.di.StorefrontApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.CartBuyerIdentityUpdateMutation
import com.iti.pocketshop.shopify.CartCreateMutation
import com.iti.pocketshop.shopify.CartLinesAddMutation
import com.iti.pocketshop.shopify.CartLinesRemoveMutation
import com.iti.pocketshop.shopify.CartLinesUpdateMutation
import com.iti.pocketshop.shopify.GetCartQuery
import com.iti.pocketshop.shopify.fragment.CartFields
import com.iti.pocketshop.shopify.type.CartBuyerIdentityInput
import com.iti.pocketshop.shopify.type.CartLineInput
import com.iti.pocketshop.shopify.type.CartLineUpdateInput
import javax.inject.Inject

class ShopifyCartDataSourceImpl @Inject constructor(
    @param:StorefrontApolloClient
    private val apolloClient: ApolloClient
) : ShopifyCartDataSource {

    override suspend fun createCart(): PocketResult<CartFields, PocketDataError.Remote> {
        val result = apolloClient.mutation(CartCreateMutation()).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val cart = result.data.cartCreate?.cart?.cartFields
                if (cart != null) PocketResult.Success(cart)
                else PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }

    override suspend fun getCart(cartId: String): PocketResult<CartFields, PocketDataError.Remote> {
        val result = apolloClient.query(GetCartQuery(cartId)).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val cart = result.data.cart?.cartFields
                if (cart != null) PocketResult.Success(cart)
                else PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }

    override suspend fun addLines(
        cartId: String,
        variantId: String,
        quantity: Int
    ): PocketResult<CartFields, PocketDataError.Remote> {
        val line = CartLineInput(merchandiseId = variantId, quantity = Optional.present(quantity))
        val result = apolloClient.mutation(CartLinesAddMutation(cartId, listOf(line))).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val cart = result.data.cartLinesAdd?.cart?.cartFields
                if (cart != null) PocketResult.Success(cart)
                else PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }

    override suspend fun removeLines(
        cartId: String,
        lineIds: List<String>
    ): PocketResult<CartFields, PocketDataError.Remote> {
        val result = apolloClient.mutation(CartLinesRemoveMutation(cartId, lineIds)).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val cart = result.data.cartLinesRemove?.cart?.cartFields
                if (cart != null) PocketResult.Success(cart)
                else PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }

    override suspend fun updateLines(
        cartId: String,
        lineId: String,
        quantity: Int
    ): PocketResult<CartFields, PocketDataError.Remote> {
        val line = CartLineUpdateInput(id = lineId, quantity = Optional.present(quantity))
        val result = apolloClient.mutation(CartLinesUpdateMutation(cartId, listOf(line))).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val cart = result.data.cartLinesUpdate?.cart?.cartFields
                if (cart != null) PocketResult.Success(cart)
                else PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }

    override suspend fun linkBuyerIdentity(
        cartId: String,
        customerAccessToken: String
    ): PocketResult<CartFields, PocketDataError.Remote> {
        val identity = CartBuyerIdentityInput(customerAccessToken = Optional.present(customerAccessToken))
        val result = apolloClient.mutation(CartBuyerIdentityUpdateMutation(cartId, identity)).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val cart = result.data.cartBuyerIdentityUpdate?.cart?.cartFields
                if (cart != null) PocketResult.Success(cart)
                else PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }
}
