package com.iti.pocketshop.features.orders.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.GetCustomerOrdersQuery
import javax.inject.Inject

class OrdersRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : OrdersRemoteDataSource {

    override suspend fun getOrders(
        accessToken: String,
        pageSize: Int,
        after: String?,
    ): PocketResult<GetCustomerOrdersQuery.Customer?, PocketDataError.Remote> =
        apolloClient
            .query(
                GetCustomerOrdersQuery(
                    customerAccessToken = accessToken,
                    first = pageSize,
                    after = after?.let { Optional.Present(it) } ?: Optional.Absent,
                )
            )
            .safeCall()
            .map { data -> data.customer }
}
