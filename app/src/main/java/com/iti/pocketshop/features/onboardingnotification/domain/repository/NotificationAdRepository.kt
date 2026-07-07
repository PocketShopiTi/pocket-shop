package com.iti.pocketshop.features.onboardingnotification.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.onboardingnotification.domain.model.NotificationAd

interface NotificationAdRepository {
    suspend fun getNotificationAd(adId: String): PocketResult<NotificationAd?, PocketDataError.Auth>
}
