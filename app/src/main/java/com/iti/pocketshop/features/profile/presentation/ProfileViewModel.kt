package com.iti.pocketshop.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileLoadUpdate
import com.iti.pocketshop.features.profile.domain.usecase.GetProfileUseCase
import com.iti.pocketshop.features.profile.domain.usecase.ProfileLogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val signOutUseCase: ProfileLogOutUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var profileLoadJob: Job? = null
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
            ProfileAction.Refresh -> loadProfile()
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
        if (profileLoadJob?.isActive == true) return

        profileLoadJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            try {
                getProfile().collect { profileResult ->
                    when (profileResult) {

                        is ProfileLoadUpdate.Cached -> _state.update { current ->
                            current.copy(
                                profile = profileResult.profile,
                                error = null,
                            )
                        }

                        is ProfileLoadUpdate.Fresh -> _state.update {
                            it.copy(profile = profileResult.profile, error = null)
                        }

                        is ProfileLoadUpdate.Failed -> _state.update {
                            it.copy(error = profileResult.error)
                        }
                    }
                }
            } finally {
                _state.update { it.copy(isRefreshing = false) }
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
