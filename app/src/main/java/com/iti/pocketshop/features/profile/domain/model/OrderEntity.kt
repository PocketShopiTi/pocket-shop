package com.iti.pocketshop.features.profile.domain.model

import java.util.Date

data class OrderEntity(
    val id: String,
    val status: OrderStatus,
    val price: Double,
    val date: Date,
    val imageUrl: String?
)
