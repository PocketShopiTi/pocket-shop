package com.iti.pocketshop.features.onboardingnotification.data.mapper

import com.iti.pocketshop.features.onboardingnotification.data.dto.NotificationAdDto
import com.iti.pocketshop.features.onboardingnotification.domain.model.NotificationAd

fun NotificationAdDto.toDomain(): NotificationAd =
    NotificationAd(
        id = id,
        title = title,
        description = description,
        couponCode = couponCode,
        buttonText = buttonText,
        imageUrl = imageUrl,
        active = active,
    )
