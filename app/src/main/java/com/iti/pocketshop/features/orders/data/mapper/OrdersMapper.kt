package com.iti.pocketshop.features.orders.data.mapper

import com.iti.pocketshop.features.orders.domain.model.OrderItem
import com.iti.pocketshop.features.orders.domain.model.OrderStatus
import com.iti.pocketshop.features.orders.domain.model.OrdersPage
import com.iti.pocketshop.shopify.GetCustomerOrdersQuery
import com.iti.pocketshop.shopify.type.OrderFinancialStatus
import com.iti.pocketshop.shopify.type.OrderFulfillmentStatus
import java.time.Instant

fun GetCustomerOrdersQuery.Orders.toDomain(): OrdersPage =
    OrdersPage(
        orders = edges.map { edge -> edge.node.toDomain() },
        endCursor = pageInfo.endCursor,
        hasNextPage = pageInfo.hasNextPage && pageInfo.endCursor != null,
    )

private fun GetCustomerOrdersQuery.Node.toDomain(): OrderItem =
    OrderItem(
        id = id,
        name = name,
        processedAtEpochMillis = processedAt.toEpochMillisOrZero(),
        status = toOrderHistoryStatus(),
        firstItemTitle = lineItems.edges.firstOrNull()?.node?.title,
        imageUrl = lineItems.edges.firstOrNull()?.node?.variant?.image?.url,
        total = currentTotalPrice.amount,
        currencyCode = currentTotalPrice.currencyCode.name,
    )

private fun GetCustomerOrdersQuery.Node.toOrderHistoryStatus(): OrderStatus {
    if (financialStatus == OrderFinancialStatus.VOIDED) {
        return OrderStatus.CANCELLED
    }

    return when (fulfillmentStatus) {
        OrderFulfillmentStatus.FULFILLED -> OrderStatus.FULFILLED

        OrderFulfillmentStatus.IN_PROGRESS,
        OrderFulfillmentStatus.PARTIALLY_FULFILLED,
        OrderFulfillmentStatus.PENDING_FULFILLMENT -> OrderStatus.PROCESSING

        OrderFulfillmentStatus.UNFULFILLED,
        OrderFulfillmentStatus.OPEN,
        OrderFulfillmentStatus.ON_HOLD,
        OrderFulfillmentStatus.RESTOCKED,
        OrderFulfillmentStatus.SCHEDULED,
        OrderFulfillmentStatus.UNKNOWN__ -> OrderStatus.PENDING
    }
}

private fun String.toEpochMillisOrZero(): Long =
    runCatching { Instant.parse(this).toEpochMilli() }.getOrDefault(0L)
