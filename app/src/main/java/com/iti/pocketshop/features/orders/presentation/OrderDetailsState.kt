package com.iti.pocketshop.features.orders.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.features.orders.domain.model.OrderDetails

data class OrderDetailsState(
    val orderId: String = "",
    val order: OrderDetails? = null,
    val isLoading: Boolean = false,
    val error: PocketDataError? = null,
)
