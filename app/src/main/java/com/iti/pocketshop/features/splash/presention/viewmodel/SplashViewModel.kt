package com.iti.pocketshop.features.splash.presention.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.splash.presention.action.SplashAction
import com.iti.pocketshop.features.splash.presention.action.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state
        .onStart { navigateToLogin() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SplashState()
        )

    private val _events = Channel<SplashAction>()
    val events = _events.receiveAsFlow()

    fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.NavigateToLogin -> navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            _events.send(SplashAction.NavigateToLogin)
        }
    }
}