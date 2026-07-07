package com.iti.pocketshop.features.profile.data.mapper

import com.iti.pocketshop.features.orders.domain.OrderTimeline
import com.iti.pocketshop.features.orders.domain.OrderTimelinePhase
import com.iti.pocketshop.features.orders.domain.toEpochMillisOrZero
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.shopify.GetProfileQuery
import com.iti.pocketshop.shopify.type.OrderFulfillmentStatus


internal fun GetProfileQuery.Customer.toOrdersList(): List<OrderEntity> =
    orders.nodes.map { order ->
        OrderEntity(
            id = order.id,
            name = order.name,
            status = order.toOrderStatus(),
            total = order.currentTotalPrice.amount,
            currencyCode = order.currentTotalPrice.currencyCode.name,
            imageUrl = order.lineItems.edges.firstOrNull()?.node?.variant?.image?.url,
        )
    }

private fun GetProfileQuery.Node1.toOrderStatus(): OrderStatus {
    if (fulfillmentStatus == OrderFulfillmentStatus.FULFILLED) {
        return OrderStatus.FULFILLED
    }
    val phase = OrderTimeline.phaseFor(
        processedAtEpochMillis = processedAt.toEpochMillisOrZero(),
        nowEpochMillis = System.currentTimeMillis(),
    )
    return when (phase) {
        OrderTimelinePhase.ORDERED -> OrderStatus.ORDERED
        OrderTimelinePhase.PROCESSING -> OrderStatus.PROCESSING
        OrderTimelinePhase.DELIVERED -> OrderStatus.FULFILLED
    }
}
