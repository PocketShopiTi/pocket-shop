package com.iti.pocketshop.features.auth.otp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.sessionmanager.domain.usecase.GetCurrentUserSessionUseCase
import com.iti.pocketshop.common.sessionmanager.domain.usecase.SignOutUseCase
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.otp.domain.ResendVerificationEmailUseCase
import com.iti.pocketshop.features.auth.shared.CheckEmailVerificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EmailVerificationEvent {
    data object NavigateLogin : EmailVerificationEvent
}

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    getCurrentUserSession: GetCurrentUserSessionUseCase,
    private val checkEmailVerification: CheckEmailVerificationUseCase,
    private val resendVerificationEmail: ResendVerificationEmailUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        EmailVerificationState(email = getCurrentUserSession()?.email.orEmpty())
    )
    val state = _state.asStateFlow()

    private val _events = Channel<EmailVerificationEvent>()
    val events = _events.receiveAsFlow()
    private var cooldownJob: Job? = null

    fun onAction(action: EmailVerificationAction) {
        when (action) {
            EmailVerificationAction.CheckVerification -> check()
            EmailVerificationAction.ResendEmail -> resend()
            EmailVerificationAction.BackToLogin -> backToLogin()
        }
    }

    private fun check() {
        if (_state.value.isChecking) return
        viewModelScope.launch {
            _state.update {
                it.copy(isChecking = true, error = null, isNotVerifiedYet = false)
            }
            when (val result = checkEmailVerification()) {
                is PocketResult.Error -> _state.update {
                    it.copy(isChecking = false, error = result.error)
                }

                is PocketResult.Success -> if (result.data) {
                    _state.update { it.copy(isChecking = false) }
                    signOut()
                    _events.send(EmailVerificationEvent.NavigateLogin)
                } else {
                    _state.update { it.copy(isChecking = false, isNotVerifiedYet = true) }
                }
            }
        }
    }

    private fun resend() {
        if (_state.value.resendCooldown > 0) return
        viewModelScope.launch {
            _state.update { it.copy(error = null, resendSucceeded = false) }
            when (val result = resendVerificationEmail()) {
                is PocketResult.Error -> _state.update { it.copy(error = result.error) }
                is PocketResult.Success -> {
                    _state.update {
                        it.copy(
                            resendSucceeded = true,
                            resendCooldown = COOLDOWN_SECONDS
                        )
                    }
                    startCooldown()
                }
            }
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            while (_state.value.resendCooldown > 0) {
                delay(1_000)
                _state.update { it.copy(resendCooldown = (it.resendCooldown - 1).coerceAtLeast(0)) }
            }
        }
    }

    private fun backToLogin() {
        viewModelScope.launch {
            signOut()
            _events.send(EmailVerificationEvent.NavigateLogin)
        }
    }

    private companion object {
        const val COOLDOWN_SECONDS = 60
    }
}
