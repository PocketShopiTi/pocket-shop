package com.iti.pocketshop.features.orders.presentation

sealed interface OrdersAction {
    data object LoadNextPage : OrdersAction
    data object Retry : OrdersAction
}
