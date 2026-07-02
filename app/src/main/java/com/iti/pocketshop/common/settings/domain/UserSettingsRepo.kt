package com.iti.pocketshop.common.settings.domain

import com.iti.pocketshop.common.settings.domain.models.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepo {
    val settingsFlow: Flow<UserSettings>
    suspend fun updateUserSettings(updateBlock: (UserSettings) -> UserSettings)
}