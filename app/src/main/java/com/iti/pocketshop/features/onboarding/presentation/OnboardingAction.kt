package com.iti.pocketshop.features.onboarding.presentation

sealed interface OnboardingAction {
    data object Next : OnboardingAction
    data object Skip : OnboardingAction
    data object Login : OnboardingAction
    data object Guest : OnboardingAction
    data class SwipePage(val page: Int) : OnboardingAction
}