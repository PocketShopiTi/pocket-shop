package com.iti.pocketshop.features.home.domain.models

data class PromotionAd(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val couponCode: String,
    val buttonText: String,
)
