package com.iti.pocketshop.features.onboarding.presentation

import com.iti.pocketshop.features.onboarding.presentation.model.OnboardingPage
import com.iti.pocketshop.features.onboarding.presentation.model.defaultPages

data class OnboardingState(
    val pages: List<OnboardingPage> = defaultPages,
    val currentPage: Int = 0,
) {
    val isLastPage get() = currentPage == pages.lastIndex
}