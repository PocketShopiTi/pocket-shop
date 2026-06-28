package com.iti.pocketshop.features.login.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.login.domain.model.LoginError
import com.iti.pocketshop.features.login.domain.model.LoginResult
import com.iti.pocketshop.features.login.domain.usecase.ContinueAsGuestUseCase
import com.iti.pocketshop.features.login.domain.usecase.LoginWithEmailUseCase
import com.iti.pocketshop.features.login.domain.usecase.LoginWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val continueAsGuestUseCase: ContinueAsGuestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.LoginClicked              -> login()
            is LoginAction.GoogleLoginSubmitted      -> googleLogin(action.idToken)
            is LoginAction.GoogleSignInFailed        -> handleGoogleSignInError()
            is LoginAction.ContinueAsGuestClicked    -> continueAsGuest()
            is LoginAction.EmailChanged              -> processEmailChange(action)
            is LoginAction.PasswordChanged           -> processPasswordChanged(action)
            is LoginAction.TogglePasswordVisibility  -> togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun processPasswordChanged(action: LoginAction.PasswordChanged) {
        _state.update { it.copy(password = action.value, passwordError = null) }
    }

    private fun processEmailChange(action: LoginAction.EmailChanged) {
        _state.update { it.copy(email = action.value, emailError = null) }
    }

    private fun login() {
        viewModelScope.launch {
            val email = _state.value.email
            val password = _state.value.password

            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _state.update { it.copy(emailError = LoginError.INVALID_EMAIL) }
                return@launch
            }

            if (password.isBlank() || password.length < 6) {
                _state.update { it.copy(passwordError = LoginError.PASSWORD_TOO_SHORT) }
                return@launch
            }

            _state.update { it.copy(isLoading = true, generalError = null, passwordError = null, emailError = null) }

            val result = loginWithEmailUseCase(email = email, password = password)

            _state.update {
                when (result) {
                    is LoginResult.Success -> it.copy(isLoading = false, generalError = null, isLoginSuccessful = true)
                    is LoginResult.Error   -> it.copy(isLoading = false, generalError = result.error)
                }
            }
        }
    }

    private fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = loginWithGoogleUseCase(idToken)

            _state.update { currentState ->
                when (result) {
                    is LoginResult.Success -> currentState.copy(isLoading = false, isLoginSuccessful = true)
                    is LoginResult.Error   -> currentState.copy(isLoading = false, generalError = result.error)
                }
            }
        }
    }

    private fun handleGoogleSignInError() {
        _state.update { it.copy(isLoading = false, generalError = LoginError.GOOGLE_SIGN_IN_FAILED) }
    }

    private fun continueAsGuest() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = continueAsGuestUseCase()

            _state.update {
                when (result) {
                    is LoginResult.Success -> it.copy(isLoading = false, isLoginSuccessful = true)
                    is LoginResult.Error   -> it.copy(isLoading = false, generalError = result.error)
                }
            }
        }
    }


}