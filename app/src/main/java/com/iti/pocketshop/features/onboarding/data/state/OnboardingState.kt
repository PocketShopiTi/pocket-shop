package com.iti.pocketshop.features.onboarding.data.state

import com.iti.pocketshop.features.onboarding.data.datasource.defaultPages
import com.iti.pocketshop.features.onboarding.domain.model.OnboardingPage
import kotlin.collections.lastIndex

data class OnboardingState(
    val pages: List<OnboardingPage> = defaultPages,
    val currentPage: Int = 0,
) {
    val isLastPage get() = currentPage == pages.lastIndex
}
