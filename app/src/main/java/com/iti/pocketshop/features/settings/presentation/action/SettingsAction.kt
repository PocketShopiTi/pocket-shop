package com.iti.pocketshop.features.settings.presentation.action

import com.iti.pocketshop.common.settings.domain.models.CurrencySetting
import com.iti.pocketshop.common.settings.domain.models.ThemeSetting

sealed interface SettingsAction {
    data class UpdateTheme(
        val theme: ThemeSetting
    ) : SettingsAction

    data class LanguageDialogToggle(val open: Boolean) : SettingsAction
    data class ThemeDialogToggle(val open: Boolean) : SettingsAction
    data class UpdateCurrencyUnit(
        val currencyUnit: CurrencySetting
    ) : SettingsAction
}