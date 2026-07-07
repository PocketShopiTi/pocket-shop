package com.iti.pocketshop.features.orders.data.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.shopify.GetCustomerOrdersQuery
import com.iti.pocketshop.shopify.GetOrderDetailsQuery

interface OrdersRemoteDataSource {
    suspend fun getOrders(
        accessToken: String,
        pageSize: Int,
        after: String?,
    ): PocketResult<GetCustomerOrdersQuery.Customer?, PocketDataError.Remote>

    suspend fun getOrderDetails(
        orderId: String,
    ): PocketResult<GetOrderDetailsQuery.OnOrder?, PocketDataError.Remote>
}
