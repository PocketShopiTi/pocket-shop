package com.iti.pocketshop.features.profile.data.datasource.shopify

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
import com.iti.pocketshop.network.safeCall
import com.iti.pocketshop.shopify.GetProfileQuery
import javax.inject.Inject

class ShopifyDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : ShopifyDataSource {
    override suspend fun getUserProfile(
        accessToken: String,
        ordersCount: Int,
    ): PocketResult<GetProfileQuery.Customer?, PocketDataError.Remote> = apolloClient
        .query(GetProfileQuery(accessToken, ordersCount = Optional.Present(ordersCount)))
        .safeCall()
        .map { data -> data.customer }
}
