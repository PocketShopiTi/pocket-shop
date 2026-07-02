package com.iti.pocketshop.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.usecase.ClearFavoritesUseCase
import com.iti.pocketshop.core.sessionmanager.domain.usecase.SignOutUseCase
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileLoadUpdate
import com.iti.pocketshop.features.profile.domain.usecase.GetProfileUseCase
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
    private val signOutUseCase: SignOutUseCase,
    private val clearFavoritesUseCase: ClearFavoritesUseCase,
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
                it.copy(showLogoutConfirmation = true)
            }

            ProfileAction.LogoutDismissed -> _state.update {
                it.copy(showLogoutConfirmation = false)
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

                        is ProfileLoadUpdate.Fresh -> _state.update { state ->
                            val profile = if (
                                state.profile is ProfileData.Authenticated &&
                                !state.profile.user.imageUrl.isNullOrEmpty() &&
                                profileResult.profile.user.imageUrl.isNullOrEmpty()
                            ) {
                                profileResult.profile.copy(
                                    user = profileResult.profile.user.copy(
                                        imageUrl = state.profile.user.imageUrl
                                    )
                                )
                            } else {
                                profileResult.profile
                            }

                            state.copy(profile = profile, error = null)
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
            _state.update { it.copy(isLoggingOut = true) }
            clearFavoritesUseCase()
            signOutUseCase()
            _state.update {
                it.copy(isLoggingOut = false, showLogoutConfirmation = false)
            }
            _events.send(ProfileEvent.LoggedOut)
        }
    }
}
