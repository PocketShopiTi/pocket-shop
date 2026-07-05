package com.iti.pocketshop.features.onboardingnotification.domain.usecase

import com.iti.pocketshop.features.onboardingnotification.domain.repository.NotificationAdRepository
import javax.inject.Inject

class GetNotificationAdUseCase @Inject constructor(
    private val repository: NotificationAdRepository,
) {
    suspend operator fun invoke(adId: String) = repository.getNotificationAd(adId)
}
