package com.iti.pocketshop.features.otp

data class OTPState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)