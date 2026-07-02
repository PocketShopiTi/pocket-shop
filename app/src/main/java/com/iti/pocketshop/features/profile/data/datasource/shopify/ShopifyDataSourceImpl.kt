package com.iti.pocketshop.features.profile.data.datasource.shopify

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.CUSTOMER_ACCESS_TOKEN
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.GetProfileQuery
import com.iti.pocketshop.shopify.LogoutCustomerMutation
import javax.inject.Inject

class ShopifyDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : ShopifyDataSource {

    override suspend fun getCustomerAccessToken(): PocketResult<String, PocketDataError.Auth> =
        PocketResult.Success(CUSTOMER_ACCESS_TOKEN)

    override suspend fun getUserProfile(
        accessToken: String,
        ordersCount: Int
    ): PocketResult<GetProfileQuery.Customer?, PocketDataError.Remote> {
        return apolloClient
            .query(
                GetProfileQuery(
                    accessToken,
                    ordersCount = Optional.Present(ordersCount)
                )
            )
            .safeCall()
            .map { data -> data.customer }

    }

    override suspend fun logOut(accessToken: String): PocketResult<Unit, PocketDataError.Remote> {
        return apolloClient
            .mutation(LogoutCustomerMutation(accessToken))
            .safeCall()
            .map {}
    }
}