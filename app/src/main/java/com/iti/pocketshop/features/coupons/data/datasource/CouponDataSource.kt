package com.iti.pocketshop.features.coupons.data.datasource

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.ApplyDiscountCodeMutation
import javax.inject.Inject

interface CouponDataSource {

    suspend fun applyCoupons(
        cartId: String,
        discountCodes: List<String>,
    ): PocketResult<ApplyDiscountCodeMutation.CartDiscountCodesUpdate, PocketDataError.Remote>
}

class CouponDataSourceImpl @Inject constructor(
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
}
