package com.iti.pocketshop.features.orders.data.mapper

import com.iti.pocketshop.features.orders.domain.model.OrderItem
import com.iti.pocketshop.features.orders.domain.OrderTimeline
import com.iti.pocketshop.features.orders.domain.OrderTimelinePhase
import com.iti.pocketshop.features.orders.domain.toEpochMillisOrZero
import com.iti.pocketshop.features.orders.domain.model.OrderStatus
import com.iti.pocketshop.features.orders.domain.model.OrdersPage
import com.iti.pocketshop.features.orders.domain.model.OrderDetails
import com.iti.pocketshop.features.orders.domain.model.OrderDetailsAddress
import com.iti.pocketshop.features.orders.domain.model.OrderDetailsLineItem
import com.iti.pocketshop.features.orders.domain.model.OrderPaymentStatus
import com.iti.pocketshop.features.orders.domain.model.OrderPriceBreakdown
import com.iti.pocketshop.features.orders.domain.model.OrderSelectedOption
import com.iti.pocketshop.shopify.GetCustomerOrdersQuery
import com.iti.pocketshop.shopify.GetOrderDetailsQuery
import com.iti.pocketshop.shopify.type.OrderFinancialStatus
import com.iti.pocketshop.shopify.type.OrderFulfillmentStatus

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

fun GetOrderDetailsQuery.OnOrder.toDetailsDomain(): OrderDetails =
    OrderDetails(
        id = id,
        name = name,
        processedAtEpochMillis = processedAt.toEpochMillisOrZero(),
        status = toOrderDetailsStatus(),
        lineItems = lineItems.edges.map { edge ->
            val node = edge.node
            OrderDetailsLineItem(
                productId = node.variant?.product?.id,
                title = node.title,
                vendor = node.variant?.product?.vendor?.takeIf { it.isNotBlank() },
                quantity = node.currentQuantity.takeIf { it > 0 } ?: node.quantity,
                selectedOptions = node.variant
                    ?.selectedOptions
                    ?.filter { option -> option.value.isNotBlank() && option.value != "Default Title" }
                    ?.map { option ->
                        OrderSelectedOption(
                            name = option.name,
                            value = option.value,
                        )
                    }
                    .orEmpty(),
                imageUrl = node.variant?.image?.url,
                imageAltText = node.variant?.image?.altText,
                total = node.discountedTotalPrice.amount,
                currencyCode = node.discountedTotalPrice.currencyCode.name,
            )
        },
        shippingAddress = shippingAddress?.let { address ->
            val recipientName = listOfNotNull(address.firstName, address.lastName)
                .joinToString(" ")
                .trim()
            val addressLines = listOfNotNull(
                address.address1,
                address.address2,
                address.city,
                address.province,
                address.country,
                address.zip,
            ).filter { line -> line.isNotBlank() }

            if (recipientName.isBlank() && addressLines.isEmpty() && address.phone.isNullOrBlank()) {
                null
            } else {
                OrderDetailsAddress(
                    recipientName = recipientName,
                    addressLines = addressLines,
                    phone = address.phone,
                )
            }
        },
        priceBreakdown = toPriceBreakdownDomain(),
    )

private fun GetOrderDetailsQuery.OnOrder.toPriceBreakdownDomain(): OrderPriceBreakdown {
    val lineDiscount = lineItems.edges.sumOf { edge ->
        edge.node.discountAllocations.sumOf { allocation -> allocation.allocatedAmount.amount }
    }
    val shippingDiscount = shippingDiscountAllocations.sumOf { allocation ->
        allocation.allocatedAmount.amount
    }

    return OrderPriceBreakdown(
        subtotalBeforeDiscount = lineItems.edges.sumOf { edge ->
            edge.node.originalTotalPrice.amount
        },
        discount = lineDiscount + shippingDiscount,
        discountLabel = discountLabels(),
        shipping = currentTotalShippingPrice.amount,
        tax = currentTotalTax.amount,
        total = currentTotalPrice.amount,
        currencyCode = currentTotalPrice.currencyCode.name,
        paymentStatus = toPaymentStatus(),
    )
}

private fun GetOrderDetailsQuery.OnOrder.discountLabels(): String? =
    discountApplications.nodes.mapNotNull { node ->
        node.onDiscountCodeApplication?.code
            ?: node.onAutomaticDiscountApplication?.title
            ?: node.onManualDiscountApplication?.title
            ?: node.onScriptDiscountApplication?.title
    }
        .distinct()
        .takeIf { labels -> labels.isNotEmpty() }
        ?.joinToString()

private fun GetOrderDetailsQuery.OnOrder.toPaymentStatus(): OrderPaymentStatus =
    when (financialStatus) {
        OrderFinancialStatus.PAID -> OrderPaymentStatus.PAID

        OrderFinancialStatus.PENDING,
        OrderFinancialStatus.AUTHORIZED,
        OrderFinancialStatus.PARTIALLY_PAID -> OrderPaymentStatus.CASH_ON_DELIVERY

        OrderFinancialStatus.REFUNDED,
        OrderFinancialStatus.PARTIALLY_REFUNDED -> OrderPaymentStatus.REFUNDED

        else -> OrderPaymentStatus.UNKNOWN
    }

private fun GetCustomerOrdersQuery.Node.toOrderHistoryStatus(): OrderStatus =
    deriveTimelineStatus(
        financialStatus = financialStatus,
        fulfillmentStatus = fulfillmentStatus,
        processedAtEpochMillis = processedAt.toEpochMillisOrZero(),
    )

private fun GetOrderDetailsQuery.OnOrder.toOrderDetailsStatus(): OrderStatus =
    deriveTimelineStatus(
        financialStatus = financialStatus,
        fulfillmentStatus = fulfillmentStatus,
        processedAtEpochMillis = processedAt.toEpochMillisOrZero(),
    )

private fun deriveTimelineStatus(
    financialStatus: OrderFinancialStatus?,
    fulfillmentStatus: OrderFulfillmentStatus,
    processedAtEpochMillis: Long,
): OrderStatus {
    if (financialStatus == OrderFinancialStatus.VOIDED) {
        return OrderStatus.CANCELLED
    }
    if (fulfillmentStatus == OrderFulfillmentStatus.FULFILLED) {
        return OrderStatus.FULFILLED
    }
    val phase = OrderTimeline.phaseFor(
        processedAtEpochMillis = processedAtEpochMillis,
        nowEpochMillis = System.currentTimeMillis(),
    )
    return when (phase) {
        OrderTimelinePhase.ORDERED -> OrderStatus.ORDERED
        OrderTimelinePhase.PROCESSING -> OrderStatus.PROCESSING
        OrderTimelinePhase.DELIVERED -> OrderStatus.FULFILLED
    }
}
