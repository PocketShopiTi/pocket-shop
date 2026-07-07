package com.iti.pocketshop.features.onboardingnotification.data.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.onboardingnotification.data.datasource.NotificationAdRemoteDataSource
import com.iti.pocketshop.features.onboardingnotification.data.mapper.toDomain
import com.iti.pocketshop.features.onboardingnotification.domain.model.NotificationAd
import com.iti.pocketshop.features.onboardingnotification.domain.repository.NotificationAdRepository
import javax.inject.Inject

class NotificationAdRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationAdRemoteDataSource,
) : NotificationAdRepository {

    override suspend fun getNotificationAd(
        adId: String,
    ): PocketResult<NotificationAd?, PocketDataError.Auth> =
        remoteDataSource.getNotificationAd(adId).map { dto -> dto?.toDomain() }
}
