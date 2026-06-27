package com.iti.pocketshop.features.profile

data class ProfileState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)