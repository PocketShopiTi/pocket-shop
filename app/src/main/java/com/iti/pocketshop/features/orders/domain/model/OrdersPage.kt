package com.iti.pocketshop.features.orders.domain.model

data class OrdersPage(
    val orders: List<OrderItem>,
    val endCursor: String?,
    val hasNextPage: Boolean,
)
