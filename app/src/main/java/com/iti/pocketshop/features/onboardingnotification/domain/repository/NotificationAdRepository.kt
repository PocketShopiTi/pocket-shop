package com.iti.pocketshop.features.onboardingnotification.domain.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.onboardingnotification.domain.model.NotificationAd

interface NotificationAdRepository {
    suspend fun getNotificationAd(adId: String): PocketResult<NotificationAd?, PocketDataError.Auth>
}
