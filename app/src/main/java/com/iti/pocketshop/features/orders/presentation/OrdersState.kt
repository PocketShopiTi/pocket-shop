package com.iti.pocketshop.features.orders.presentation

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.features.orders.domain.model.OrderItem

data class OrdersState(
    val orders: List<OrderItem> = emptyList(),
    val isInitialLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = true,
    val endCursor: String? = null,
    val error: PocketDataError? = null,
)
