package com.iti.pocketshop.features.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.settings.domain.UserSettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OnboardingEvent {
    data object NavigateToLogin : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userSettingsRepo: UserSettingsRepo,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = OnboardingState()
    )

    private val _events = Channel<OnboardingEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.Next -> onboardingAction()
            OnboardingAction.Login -> navigate()
            is OnboardingAction.SwipePage -> onboardingActionSwipe(action)
        }
    }

    private fun onboardingActionSwipe(action: OnboardingAction.SwipePage) {
        _state.update { it.copy(currentPage = action.page) }
    }

    private fun onboardingAction() {
        val current = _state.value
        if (current.isLastPage) navigate()
        else _state.update { it.copy(currentPage = it.currentPage + 1) }
    }

    private fun navigate() {
        saveOnboardingShown()
        viewModelScope.launch { _events.send(OnboardingEvent.NavigateToLogin) }
    }

    private fun saveOnboardingShown() {
        viewModelScope.launch {
            userSettingsRepo.updateUserSettings {
                it.copy(hasSeenOnboarding = true)
            }
        }
    }
}
