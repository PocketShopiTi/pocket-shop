package com.iti.pocketshop.features.orders.data

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.orders.domain.model.OrderItem
import com.iti.pocketshop.features.orders.domain.model.OrderStatus
import com.iti.pocketshop.features.orders.domain.model.OrdersPage
import com.iti.pocketshop.features.orders.domain.repository.OrdersRepository
import java.time.Instant
import javax.inject.Inject

class MockOrdersRepository @Inject constructor() : OrdersRepository {

    override suspend fun getOrdersPage(
        pageSize: Int,
        after: String?,
    ): PocketResult<OrdersPage, PocketDataError> {
        val startIndex = after
            ?.toIntOrNull()
            ?.coerceIn(0, mockOrders.size)
            ?: 0
        val endIndex = (startIndex + pageSize.coerceAtLeast(1))
            .coerceAtMost(mockOrders.size)
        val hasNextPage = endIndex < mockOrders.size

        return PocketResult.Success(
            OrdersPage(
                orders = mockOrders.subList(startIndex, endIndex),
                endCursor = endIndex.toString().takeIf { hasNextPage },
                hasNextPage = hasNextPage,
            )
        )
    }

    private companion object {
        const val MOCK_ORDER_COUNT = 14
        const val DAY_MILLIS = 86_400_000L

        val statuses = listOf(
            OrderStatus.FULFILLED,
            OrderStatus.PROCESSING,
            OrderStatus.PENDING,
            OrderStatus.CANCELLED,
        )

        val productTitles = listOf(
            "Canvas sneakers",
            "Structured tote bag",
            "Silk midi dress",
            "Linen blazer",
        )

        val mockOrders = List(MOCK_ORDER_COUNT) { index ->
            OrderItem(
                id = "mock-order-$index",
                name = "PK-2026-${(847 - index * 37).toString().padStart(4, '0')}",
                processedAtEpochMillis =
                    Instant.parse("2026-06-18T00:00:00Z").toEpochMilli() - index * 11 * DAY_MILLIS,
                status = statuses[index % statuses.size],
                firstItemTitle = productTitles[index % productTitles.size],
                imageUrl = null,
                total = 149.0 + index * 47.25,
                currencyCode = "USD",
            )
        }
    }
}
