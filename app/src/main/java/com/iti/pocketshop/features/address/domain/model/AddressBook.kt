package com.iti.pocketshop.features.address.domain.model

data class AddressBook(
    val customerName: String,
    val addresses: List<Address>,
    val defaultAddressId: String?,
)
