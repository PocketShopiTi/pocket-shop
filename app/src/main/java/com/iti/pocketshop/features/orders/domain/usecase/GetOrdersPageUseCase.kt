package com.iti.pocketshop.features.orders.domain.usecase

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.orders.domain.model.OrdersPage
import com.iti.pocketshop.features.orders.domain.repository.OrdersRepository
import javax.inject.Inject

class GetOrdersPageUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(
        pageSize: Int,
        after: String?,
    ): PocketResult<OrdersPage, PocketDataError> =
        repository.getOrdersPage(pageSize = pageSize, after = after)
}
