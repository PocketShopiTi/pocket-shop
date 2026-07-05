package com.iti.pocketshop.features.address.domain.model

data class Address(
    val id: String,
    val firstName: String,
    val lastName: String,
    val company: String,
    val phone: String,
    val address1: String,
    val address2: String,
    val city: String,
    val province: String,
    val zip: String,
    val country: String,
    val countryCode: String,
    val provinceCode: String,
    val formattedArea: String,
    val latitude: Double?,
    val longitude: Double?,
    val isDefault: Boolean,
) {
    val recipientName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { company.ifBlank { "Delivery address" } }

    val streetLine: String
        get() = listOf(address1, address2)
            .filter { it.isNotBlank() }
            .joinToString(", ")

    val locationLine: String
        get() = listOf(city, province, zip, country)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
