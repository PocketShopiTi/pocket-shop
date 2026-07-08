package com.iti.pocketshop.features.auth.login.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.usecase.SyncFavoritesUseCase
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.login.domain.model.LoginOutcome
import com.iti.pocketshop.features.auth.login.domain.usecase.ContinueAsGuestUseCase
import com.iti.pocketshop.features.auth.login.domain.usecase.LoginWithEmailUseCase
import com.iti.pocketshop.features.auth.login.domain.usecase.LoginWithGoogleUseCase
import com.iti.pocketshop.features.cart.domain.usecase.RestoreOrCreateCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginEvent {
    data object NavigateHome : LoginEvent
    data object NavigateVerification : LoginEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val continueAsGuestUseCase: ContinueAsGuestUseCase,
    private val syncFavorites: SyncFavoritesUseCase,
    private val restoreCart: RestoreOrCreateCartUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.LoginClicked -> login()
            is LoginAction.GoogleLoginSubmitted -> googleLogin(action.idToken)
            LoginAction.GoogleSignInFailed -> {
                viewModelScope.launch {
                    ErrorDialogController.sendEvent(PocketDataError.Auth.UNKNOWN)
                }
                _state.update {
                    it.copy(isLoading = false)
                }
            }

            LoginAction.ContinueAsGuestClicked -> continueAsGuest()
            is LoginAction.EmailChanged -> _state.update {
                it.copy(email = action.value, emailError = false, generalError = null)
            }

            is LoginAction.PasswordChanged -> _state.update {
                it.copy(password = action.value, passwordError = false, generalError = null)
            }

            LoginAction.TogglePasswordVisibility -> _state.update {
                it.copy(isPasswordVisible = !it.isPasswordVisible)
            }
        }
    }

    private fun login() {
        val email = _state.value.email
        val password = _state.value.password
        val emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordError = password.length < 6
        _state.update { it.copy(emailError = emailError, passwordError = passwordError) }
        if (emailError || passwordError) return

        launchLogin { loginWithEmailUseCase(email, password) }
    }

    private fun googleLogin(idToken: String) = launchLogin {
        loginWithGoogleUseCase(idToken)
    }

    private fun continueAsGuest() = launchLogin {
        continueAsGuestUseCase()
    }

    private fun launchLogin(
        block: suspend () -> PocketResult<LoginOutcome, PocketDataError>,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            when (val result = block()) {
                is PocketResult.Error -> _state.update {
                    it.copy(isLoading = false, generalError = result.error)
                }

                is PocketResult.Success -> {
                    when (result.data) {
                        LoginOutcome.Ready -> {
                            val j1 = launch {
                                syncFavorites()
                            }
                            val j2 = launch {
                                restoreCart()
                            }
                            joinAll(j1, j2)
                            _state.update { it.copy(isLoading = false) }
                            _events.send(LoginEvent.NavigateHome)
                        }
                        LoginOutcome.NeedsEmailVerification -> {
                            _state.update { it.copy(isLoading = false) }
                            _events.send(LoginEvent.NavigateVerification)
                        }
                    }
                }
            }
        }
    }
}
