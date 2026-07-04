package com.iti.pocketshop.features.checkout.domain.model

data class CheckoutAddress(
    val id: String,
    val firstName: String,
    val lastName: String,
    val address1: String,
    val address2: String,
    val city: String,
    val country: String,
    val phone: String
)
