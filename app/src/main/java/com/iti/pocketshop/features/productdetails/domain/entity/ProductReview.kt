package com.iti.pocketshop.features.productdetails.domain.entity

import kotlinx.datetime.LocalDate

data class ProductReview(
    val id: String,
    val author: String,
    val avatarUrl: String?,
    val rating: Int,
    val date: LocalDate,
    val body: String,
)
