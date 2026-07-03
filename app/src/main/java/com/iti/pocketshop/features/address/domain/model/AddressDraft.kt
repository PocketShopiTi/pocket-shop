package com.iti.pocketshop.features.address.domain.model

data class AddressDraft(
    val firstName: String = "",
    val lastName: String = "",
    val company: String = "",
    val phone: String = "",
    val address1: String = "",
    val address2: String = "",
    val city: String = "",
    val province: String = "",
    val zip: String = "",
    val country: String = "",
    val isDefault: Boolean = false,
)
