package com.iti.pocketshop.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.usecase.GetProfileUseCase
import com.iti.pocketshop.features.profile.domain.usecase.ProfileSignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val signOutUseCase: ProfileSignOutUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadProfile()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState(),
        )

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Retry -> loadProfile()
            ProfileAction.LogoutRequested -> _state.update {
                it.copy(showLogoutConfirmation = true, logoutError = null)
            }

            ProfileAction.LogoutDismissed -> _state.update {
                it.copy(showLogoutConfirmation = false, logoutError = null)
            }

            ProfileAction.LogoutConfirmed -> signOut()
        }
    }

    private fun loadProfile() {

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getProfile()) {
                is PocketResult.Success -> _state.update {
                    it.copy(isLoading = false, profile = result.data, error = null)
                }

                is PocketResult.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    private fun signOut() {
        if (_state.value.isLoggingOut) return

        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true, logoutError = null) }

            when (val result = signOutUseCase()) {

                is PocketResult.Success -> {
                    _state.update {
                        it.copy(isLoggingOut = false, showLogoutConfirmation = false)
                    }
                    _events.send(ProfileEvent.LoggedOut)
                }

                is PocketResult.Error ->
                    _state.update {
                        it.copy(isLoggingOut = false, logoutError = result.error)
                    }
            }
        }
    }
}
