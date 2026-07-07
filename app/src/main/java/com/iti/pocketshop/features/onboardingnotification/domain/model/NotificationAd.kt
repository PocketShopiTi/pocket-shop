package com.iti.pocketshop.features.onboardingnotification.domain.model

data class NotificationAd(
    val id: String,
    val title: String,
    val description: String,
    val couponCode: String,
    val buttonText: String,
    val imageUrl: String,
    val active: Boolean,
)
