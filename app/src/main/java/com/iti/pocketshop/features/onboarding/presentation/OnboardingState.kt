package com.iti.pocketshop.features.onboarding.presentation

import com.iti.pocketshop.features.onboarding.data.datasource.defaultPages
import com.iti.pocketshop.features.onboarding.domain.model.OnboardingPage

data class OnboardingState(
    val pages: List<OnboardingPage> = defaultPages,
    val currentPage: Int = 0,
) {
    val isLastPage get() = currentPage == pages.lastIndex
}