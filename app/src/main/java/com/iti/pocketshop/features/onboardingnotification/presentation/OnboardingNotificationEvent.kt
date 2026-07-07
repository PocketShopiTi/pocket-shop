package com.iti.pocketshop.features.onboardingnotification.presentation

sealed interface OnboardingNotificationEvent {
    data object NavigateHome : OnboardingNotificationEvent
}
