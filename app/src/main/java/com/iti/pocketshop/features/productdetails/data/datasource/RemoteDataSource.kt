package com.iti.pocketshop.features.productdetails.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.iti.pocketshop.core.exceptions.AppException
import com.iti.pocketshop.core.exceptions.ShopifyExceptions
import com.iti.pocketshop.shopify.GetProductByIdQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getProductById(productId: String): GetProductByIdQuery.Product {
        val response = try {

            apolloClient.query(GetProductByIdQuery(id = productId)).execute()

        } catch (e: ApolloNetworkException) {
            throw AppException.NetworkError(e)
        } catch (e: ApolloException) {
            throw AppException.Unknown(
                message = e.message ?: "request failed",
                cause = e
            )
        }

        if (response.hasErrors()) {
            throw ShopifyExceptions.GraphQlError(
                response.errors?.joinToString { it.message }.orEmpty()
                    .ifBlank { "Shopify GraphQL error" }
            )
        }

        return response.data?.product ?: throw ShopifyExceptions.ProductNotFound(productId)
    }
}