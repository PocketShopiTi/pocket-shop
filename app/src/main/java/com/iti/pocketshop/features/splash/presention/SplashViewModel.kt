package com.iti.pocketshop.features.splash.presention

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

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