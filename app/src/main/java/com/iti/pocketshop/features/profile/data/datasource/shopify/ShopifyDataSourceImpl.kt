package com.iti.pocketshop.features.profile.data.datasource.shopify

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.di.StorefrontApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.GetProfileQuery
import javax.inject.Inject

class ShopifyDataSourceImpl @Inject constructor(
    @param:StorefrontApolloClient
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
