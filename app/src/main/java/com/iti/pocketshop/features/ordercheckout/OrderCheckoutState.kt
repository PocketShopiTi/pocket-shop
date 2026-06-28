package com.iti.pocketshop.features.ordercheckout

data class OrderCheckoutState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)