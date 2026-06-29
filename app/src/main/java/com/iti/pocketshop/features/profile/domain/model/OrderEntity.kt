package com.iti.pocketshop.features.profile.domain.model

data class OrderEntity(
    val id: String,
    val status: OrderStatus,
    val total: Double,
    val currencyCode: String,
    val imageUrl: String?,
)
