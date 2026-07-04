package com.iti.pocketshop.features.home.data

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.features.home.domain.HomeRemoteSource
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.shopify.HomeQuery
import javax.inject.Inject

class HomeRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : HomeRemoteSource {

    override suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        brandCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote> {
        return apolloClient
            .query(
                HomeQuery(
                    featuredCount = featuredCount,
                    bestSellerCount = bestSellerCount,
                    newArrivalCount = newArrivalsCount,
                    brandCount = brandCount,
                )
            )
            .safeCall()
            .map { data ->
                data.toDomain()
            }
    }
}