package com.iti.pocketshop

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.common.settings.domain.UserSettingsRepo
import com.iti.pocketshop.common.settings.domain.models.LanguageSetting
import com.iti.pocketshop.common.settings.domain.models.UserSettings
import com.iti.pocketshop.common.sessionmanager.domain.repository.UserRepo
import com.iti.pocketshop.core.connectivity.NetworkMonitor
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import com.iti.pocketshop.features.cart.domain.usecase.RestoreCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepo: UserRepo,
    private val userSettingsRepo: UserSettingsRepo,
    private val restoreCartUseCase: RestoreCartUseCase,
    networkMonitor: NetworkMonitor,
    private val cartRepository: CartRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            combine(
                userRepo.observeSession().distinctUntilChanged(),
                networkMonitor.isOnline.distinctUntilChanged()
            ) { session, isOnline ->
                Pair(session, isOnline)
            }.collect { (session, isOnline) ->
                if (session != null && !session.isAnonymous && isOnline) {
                    if (cartRepository.cartState.value == null) {
                        restoreCartUseCase()
                    }
                }
            }
        }
    }

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
}

val LocalUser = compositionLocalOf<UserSession?> { null }
val LocalSettingsUser = compositionLocalOf { UserSettings() }
