package com.iti.pocketshop.features.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.onboarding.data.state.OnboardingState
import com.iti.pocketshop.features.onboarding.presentation.action.OnboardingAction
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
class OnboardingViewModel @Inject constructor() : ViewModel() {

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
            OnboardingAction.Next -> {
                val current = _state.value
                if (current.isLastPage) navigate()
                else _state.update { it.copy(currentPage = it.currentPage + 1) }
            }
            OnboardingAction.Skip,
            OnboardingAction.Login,
            OnboardingAction.Guest -> navigate()
            is OnboardingAction.SwipePage ->
                _state.update { it.copy(currentPage = action.page) }
        }
    }

    private fun navigate() {
        viewModelScope.launch { _events.send(OnboardingEvent.NavigateToLogin) }
    }
}