package com.iti.pocketshop.features.search

data class SearchState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)