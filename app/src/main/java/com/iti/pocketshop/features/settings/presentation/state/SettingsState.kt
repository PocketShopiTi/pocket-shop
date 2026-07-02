package com.iti.pocketshop.features.settings.presentation.state

data class SettingsState(
    val showLanguageDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val shouldShowLocationPermissionDialog: Boolean = false,
)