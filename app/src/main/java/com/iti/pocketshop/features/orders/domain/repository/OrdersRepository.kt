package com.iti.pocketshop.features.orders.domain.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.orders.domain.model.OrdersPage

interface OrdersRepository {
    suspend fun getOrdersPage(
        pageSize: Int,
        after: String?,
    ): PocketResult<OrdersPage, PocketDataError>
}
