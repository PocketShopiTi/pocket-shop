package com.iti.pocketshop.features.evaluate.domain.model

data class ProductReview(
    val id: String,
    val customerId: String,
    val customerName: String,
    val rating: Int,
    val title: String,
    val body: String,
    val createdAt: String,
    val approved: Boolean
)
