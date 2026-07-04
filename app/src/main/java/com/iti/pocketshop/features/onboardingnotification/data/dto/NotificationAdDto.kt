package com.iti.pocketshop.features.onboardingnotification.data.dto

data class NotificationAdDto(
    val id: String,
    val title: String,
    val description: String,
    val couponCode: String,
    val buttonText: String,
    val imageUrl: String,
    val active: Boolean,
)
