package com.iti.pocketshop.features.coupons.data.datasource

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.di.StorefrontApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.features.cart.data.mapper.toDomain
import com.iti.pocketshop.features.cart.domain.entity.ShopifyCart
import com.iti.pocketshop.shopify.ApplyDiscountCodeMutation
import com.iti.pocketshop.shopify.CartDiscountCodesUpdateMutation
import javax.inject.Inject

interface CouponDataSource {

    suspend fun applyCoupons(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<ApplyDiscountCodeMutation.CartDiscountCodesUpdate, PocketDataError.Remote>

    suspend fun removeCoupons(
        cartId: String
    ): PocketResult<ShopifyCart?, PocketDataError.Remote>
}

class CouponDataSourceImpl @Inject constructor(
    @param:StorefrontApolloClient
    private val apolloClient: ApolloClient,
) : CouponDataSource {

    override suspend fun applyCoupons(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<ApplyDiscountCodeMutation.CartDiscountCodesUpdate, PocketDataError.Remote> {
        val result = apolloClient.mutation(
            ApplyDiscountCodeMutation(
                cartId = cartId,
                discountCodes = discountCodes,
            ),
        ).safeCall()

        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                result.data.cartDiscountCodesUpdate?.let { payload ->
                    PocketResult.Success(payload)
                }
                    ?: PocketResult.Error(PocketDataError.Remote.EMPTY_RESULT)
            }
        }
    }

    override suspend fun removeCoupons(
        cartId: String,
    ): PocketResult<ShopifyCart?, PocketDataError.Remote> {
        return apolloClient.mutation(CartDiscountCodesUpdateMutation(cartId, emptyList()))
            .safeCall()
            .map {
                it.cartDiscountCodesUpdate?.cart?.cartFields?.toDomain()
            }
    }
}
