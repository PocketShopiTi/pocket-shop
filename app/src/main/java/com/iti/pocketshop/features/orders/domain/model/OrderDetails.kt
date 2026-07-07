package com.iti.pocketshop.features.orders.domain.model

data class OrderDetails(
    val id: String,
    val name: String,
    val processedAtEpochMillis: Long,
    val status: OrderStatus,
    val lineItems: List<OrderDetailsLineItem>,
    val shippingAddress: OrderDetailsAddress?,
    val priceBreakdown: OrderPriceBreakdown,
)

data class OrderDetailsLineItem(
    val productId: String?,
    val title: String,
    val vendor: String?,
    val quantity: Int,
    val selectedOptions: List<OrderSelectedOption>,
    val imageUrl: String?,
    val imageAltText: String?,
    val total: Double,
    val currencyCode: String,
)

data class OrderSelectedOption(
    val name: String,
    val value: String,
)

data class OrderDetailsAddress(
    val recipientName: String,
    val addressLines: List<String>,
    val phone: String?,
)

data class OrderPriceBreakdown(
    val subtotalBeforeDiscount: Double,
    val discount: Double,
    val discountLabel: String?,
    val shipping: Double,
    val tax: Double,
    val total: Double,
    val currencyCode: String,
    val paymentStatus: OrderPaymentStatus,
)

enum class OrderPaymentStatus {
    PAID,
    CASH_ON_DELIVERY,
    REFUNDED,
    UNKNOWN,
}
