package com.iti.pocketshop.features.brands.data

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
import com.iti.pocketshop.network.safeCall
import com.iti.pocketshop.features.brands.domain.BrandsRemoteSource
import com.iti.pocketshop.features.brands.domain.models.BrandItem
import com.iti.pocketshop.shopify.GetBrandsQuery
import javax.inject.Inject

class BrandsRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : BrandsRemoteSource {
    override suspend fun getBrands(first: Int): PocketResult<List<BrandItem>, PocketDataError.Remote> {
        return apolloClient
            .query(GetBrandsQuery(first = first))
            .safeCall()
            .map { data ->
                data.brands.nodes.map {
                    it.toDomain()
                }
            }
    }
}
