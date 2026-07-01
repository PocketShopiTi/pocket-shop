package com.iti.pocketshop.features.profile.data.mapper

import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.shopify.GetProfileQuery
import com.iti.pocketshop.shopify.type.OrderFulfillmentStatus


internal fun GetProfileQuery.Customer.toOrdersList(): List<OrderEntity> =
    orders.edges.map {
        val order = it.node
        OrderEntity(
            id = order.id,
            status = order.fulfillmentStatus.toOrderStatus(),
            total = order.currentTotalPrice.amount,
            currencyCode = order.currentTotalPrice.currencyCode.name,
            imageUrl = order.lineItems.edges.firstOrNull()?.node?.variant?.image?.url,
        )
    }

private fun OrderFulfillmentStatus?.toOrderStatus(): OrderStatus {
    return when (this) {
        OrderFulfillmentStatus.FULFILLED -> OrderStatus.FULFILLED

        OrderFulfillmentStatus.IN_PROGRESS,
        OrderFulfillmentStatus.PARTIALLY_FULFILLED,
        OrderFulfillmentStatus.PENDING_FULFILLMENT -> OrderStatus.PROCESSING

        OrderFulfillmentStatus.UNFULFILLED,
        OrderFulfillmentStatus.OPEN,
        OrderFulfillmentStatus.ON_HOLD,
        OrderFulfillmentStatus.RESTOCKED,
        OrderFulfillmentStatus.SCHEDULED,
        null,
        OrderFulfillmentStatus.UNKNOWN__ -> OrderStatus.PENDING
    }
}
