package com.iti.pocketshop.features.orders.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.orders.domain.model.OrderDetails
import com.iti.pocketshop.features.orders.domain.model.OrdersPage

interface OrdersRepository {
    suspend fun getOrdersPage(
        pageSize: Int,
        after: String?,
    ): PocketResult<OrdersPage, PocketDataError>

    suspend fun getOrderDetails(
        orderId: String,
    ): PocketResult<OrderDetails, PocketDataError>
}
