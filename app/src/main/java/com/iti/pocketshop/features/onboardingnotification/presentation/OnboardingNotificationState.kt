package com.iti.pocketshop.features.onboardingnotification.presentation

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.features.onboardingnotification.domain.model.NotificationAd

data class OnboardingNotificationState(
    val adId: String = "",
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
    val ad: NotificationAd? = null,
    val error: PocketDataError? = null,
    val currentPage: Int = 0,
) {
    val isLastPage: Boolean
        get() = currentPage == PAGE_COUNT - 1

    val isUnavailable: Boolean
        get() = hasLoaded && !isLoading && error == null && (ad == null || !ad.active)

    companion object {
        const val PAGE_COUNT = 3
    }
}
