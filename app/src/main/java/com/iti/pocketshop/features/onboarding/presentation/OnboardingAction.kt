package com.iti.pocketshop.features.onboarding.presentation

sealed interface OnboardingAction {
    data object Next : OnboardingAction
    data object Login : OnboardingAction
    data class SwipePage(val page: Int) : OnboardingAction
}