package com.iti.pocketshop

import com.iti.pocketshop.common.settings.domain.models.UserSettings


sealed interface MainUiState {
    data object Loading : MainUiState
    data class Ready(val userSettings: UserSettings) : MainUiState
}
