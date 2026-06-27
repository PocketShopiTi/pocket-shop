package com.iti.pocketshop.features.cart

data class CartState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)