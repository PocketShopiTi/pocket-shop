package com.iti.pocketshop.features.onboardingnotification.data.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.onboardingnotification.data.dto.NotificationAdDto

interface NotificationAdRemoteDataSource {
    suspend fun getNotificationAd(adId: String): PocketResult<NotificationAdDto?, PocketDataError.Auth>
}
