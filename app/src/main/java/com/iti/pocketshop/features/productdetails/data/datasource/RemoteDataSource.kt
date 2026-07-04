package com.iti.pocketshop.features.productdetails.data.datasource

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.GetProductByIdQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopifyProductDetailsDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
) : ProductDetailsDataSource {
    override suspend fun getProductById(
        productId: String,
    ): PocketResult<GetProductByIdQuery.Product, PocketDataError.Remote> {

        val result = apolloClient
            .query(GetProductByIdQuery(id = productId))
            .safeCall()

        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> result.data.product
                ?.let { product -> PocketResult.Success(product) }
                ?: PocketResult.Error(PocketDataError.Remote.EMPTY_RESULT)
        }
    }
}