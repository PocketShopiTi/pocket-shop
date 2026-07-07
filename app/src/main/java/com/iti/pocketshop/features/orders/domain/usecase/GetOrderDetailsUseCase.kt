package com.iti.pocketshop.features.orders.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.orders.domain.model.OrderDetails
import com.iti.pocketshop.features.orders.domain.repository.OrdersRepository
import javax.inject.Inject

class GetOrderDetailsUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(
        orderId: String,
    ): PocketResult<OrderDetails, PocketDataError> =
        repository.getOrderDetails(orderId = orderId)
}
