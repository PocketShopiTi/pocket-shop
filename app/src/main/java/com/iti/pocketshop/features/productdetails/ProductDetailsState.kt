package com.iti.pocketshop.features.productdetails

data class ProductDetailsState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)