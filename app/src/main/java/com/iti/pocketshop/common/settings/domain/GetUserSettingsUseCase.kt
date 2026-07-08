package com.iti.pocketshop.common.settings.domain

import com.iti.pocketshop.common.settings.domain.models.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSettingsUseCase @Inject constructor(
    private val userSettingsRepo: UserSettingsRepo,
) {
    operator fun invoke(): Flow<UserSettings> = userSettingsRepo.settingsFlow
}
