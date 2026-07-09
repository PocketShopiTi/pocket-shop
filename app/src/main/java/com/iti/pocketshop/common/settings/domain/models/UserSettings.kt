package com.iti.pocketshop.common.settings.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val language: LanguageSetting = LanguageSetting.ENGLISH,
    val theme: ThemeSetting = ThemeSetting.FOLLOW_SYSTEM,
    val currency: CurrencySetting = CurrencySetting.EGP,
    val hasSeenTutorial: Boolean = false,
    val hasSeenHomeTutorial: Boolean = false,
    val hasSeenProductTutorial: Boolean = false,
    val hasSeenWishlistTutorial: Boolean = false,
    val hasSeenCartTutorial: Boolean = false,
)
