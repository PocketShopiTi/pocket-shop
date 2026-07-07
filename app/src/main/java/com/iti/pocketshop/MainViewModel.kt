package com.iti.pocketshop

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.common.settings.domain.UserSettingsRepo
import com.iti.pocketshop.common.settings.domain.models.LanguageSetting
import com.iti.pocketshop.common.settings.domain.models.UserSettings
import com.iti.pocketshop.common.sessionmanager.domain.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepo: UserRepo,
    private val userSettingsRepo: UserSettingsRepo,
) : ViewModel() {

    val mainUiState: Flow<MainUiState> = userSettingsRepo.settingsFlow
        .map {
            MainUiState.Ready(it)
        }

    val currentUser = userRepo.observeSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun saveLanguageSettings(languageSetting: LanguageSetting) {
        viewModelScope.launch {
            userSettingsRepo.updateUserSettings {
                it.copy(language = languageSetting)
            }
        }
    }

    fun markTutorialAsSeen() {
        viewModelScope.launch {
            userSettingsRepo.updateUserSettings {
                it.copy(hasSeenTutorial = true)
            }
        }
    }
}

val LocalUser = compositionLocalOf<UserSession?> { null }
val LocalSettingsUser = compositionLocalOf { UserSettings() }
