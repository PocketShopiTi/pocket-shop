package com.iti.pocketshop.features.home

data class HomeState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)