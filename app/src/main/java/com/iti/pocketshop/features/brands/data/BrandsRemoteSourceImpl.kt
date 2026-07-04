package com.iti.pocketshop.features.brands.data

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
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
