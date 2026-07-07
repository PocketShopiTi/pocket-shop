package com.iti.pocketshop.features.orders.presentation

sealed interface OrderDetailsAction {
    data class OrderChanged(val orderId: String) : OrderDetailsAction
    data object Retry : OrderDetailsAction
}
