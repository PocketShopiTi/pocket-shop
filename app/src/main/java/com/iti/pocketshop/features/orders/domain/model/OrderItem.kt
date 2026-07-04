package com.iti.pocketshop.features.orders.domain.model

data class OrderItem(
    val id: String,
    val name: String,
    val processedAtEpochMillis: Long,
    val status: OrderStatus,
    val firstItemTitle: String?,
    val imageUrl: String?,
    val total: Double,
    val currencyCode: String,
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    FULFILLED,
    CANCELLED,
}
