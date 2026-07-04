package com.iti.pocketshop.features.address.domain.model

data class AddressLocationDetails(
    val formattedAddress: String,
    val street: String,
    val city: String,
    val province: String,
    val country: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val countryCode: String = "",
) {
    val formattedArea: String
        get() = listOf(city, province, country)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
