package com.iti.pocketshop.features.onboarding.presentation.model

import com.iti.pocketshop.R

val defaultPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.onboarding_shop,
        titleRes = R.string.onboarding_page1_title,
        subtitleRes = R.string.onboarding_page1_subtitle,
    ),
    OnboardingPage(
        imageRes = R.drawable.onboarding_ai,
        titleRes = R.string.onboarding_page2_title,
        subtitleRes = R.string.onboarding_page2_subtitle,
    ),
    OnboardingPage(
        imageRes = R.drawable.onboarding_checkout,
        titleRes = R.string.onboarding_page3_title,
        subtitleRes = R.string.onboarding_page3_subtitle,
    ),
)
