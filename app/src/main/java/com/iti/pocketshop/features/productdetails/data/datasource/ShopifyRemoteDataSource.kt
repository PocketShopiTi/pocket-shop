package com.iti.pocketshop.features.productdetails.data.datasource

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.shopify.GetProductByIdQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopifyRemoteDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) {
    companion object {
        private const val TAG = "ShopifyRemoteDataSource"
    }

    suspend fun getProductById(productId: String): GetProductByIdQuery.Product? {
        val response = apolloClient
            .query(GetProductByIdQuery(id = productId))
            .execute()

        if (response.hasErrors()) {
            Log.e(TAG, response.errors.toString())
            throw Exception(response.errors?.joinToString { it.message })
        }

        Log.i(TAG, "data = ${response.data}")
        return response.data?.product
    }
}