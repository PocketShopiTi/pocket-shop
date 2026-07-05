package com.iti.pocketshop.features.onboardingnotification.presentation

sealed interface OnboardingNotificationAction {
    data class Load(val adId: String) : OnboardingNotificationAction
    data object Retry : OnboardingNotificationAction
    data object Next : OnboardingNotificationAction
    data object Home : OnboardingNotificationAction
    data class SwipePage(val page: Int) : OnboardingNotificationAction
}
