package com.iti.pocketshop.features.orders.data.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.shopify.GetCustomerOrdersQuery

interface OrdersRemoteDataSource {
    suspend fun getOrders(
        accessToken: String,
        pageSize: Int,
        after: String?,
    ): PocketResult<GetCustomerOrdersQuery.Customer?, PocketDataError.Remote>
}
