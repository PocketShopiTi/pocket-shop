package com.iti.pocketshop.features.splash.presention

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.sessionmanager.domain.usecase.GetCurrentUserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserSession: GetCurrentUserSessionUseCase,
) : ViewModel() {

    private val _events = Channel<SplashEvent>()
    val events = _events.receiveAsFlow()

    fun resolveSession(
        hasSeenOnboarding: Boolean,
    ) {
        val user = getCurrentUserSession()
        viewModelScope.launch {
            _events.send(
                when {
                    user == null && hasSeenOnboarding -> SplashEvent.OpenLogin
                    user == null -> SplashEvent.OpenOnboarding
                    user.isAnonymous || user.isEmailVerified -> SplashEvent.OpenHome
                    else -> SplashEvent.OpenVerification
                }
            )
        }
    }
}

sealed interface SplashEvent {
    data object OpenLogin : SplashEvent
    data object OpenOnboarding : SplashEvent
    data object OpenHome : SplashEvent
    data object OpenVerification : SplashEvent
}
