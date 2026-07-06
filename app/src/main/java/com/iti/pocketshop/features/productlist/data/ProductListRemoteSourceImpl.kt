package com.iti.pocketshop.features.productlist.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
import com.iti.pocketshop.network.safeCall
import com.iti.pocketshop.features.productlist.domain.ProductListPage
import com.iti.pocketshop.features.productlist.domain.ProductListRemoteSource
import com.iti.pocketshop.shopify.GetProductListQuery
import com.iti.pocketshop.shopify.type.ProductSortKeys
import javax.inject.Inject

class ProductListRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : ProductListRemoteSource {

    override suspend fun getProducts(
        first: Int,
        after: String?,
        sortKey: String,
        reverse: Boolean,
        query: String?,
    ): PocketResult<ProductListPage, PocketDataError.Remote> {
        return apolloClient
            .query(
                GetProductListQuery(
                    first = first,
                    after = if (after != null) Optional.present(after) else Optional.absent(),
                    sortKey = ProductSortKeys.valueOf(sortKey),
                    reverse = reverse,
                    query = if (query != null) Optional.present(query) else Optional.absent(),
                )
            )
            .safeCall()
            .map { data ->
                data.toDomain()
            }
    }
}
