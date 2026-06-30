package com.iti.pocketshop.features.productlist.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
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
    ): PocketResult<ProductListPage, PocketDataError.Remote> {
        return apolloClient
            .query(
                GetProductListQuery(
                    first = first,
                    after = if (after != null) Optional.present(after) else Optional.absent(),
                    sortKey = ProductSortKeys.valueOf(sortKey),
                    reverse = reverse,
                )
            )
            .safeCall()
            .map { data ->
                data.toDomain()
            }
    }
}
