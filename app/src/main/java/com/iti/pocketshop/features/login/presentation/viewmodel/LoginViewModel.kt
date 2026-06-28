package com.iti.pocketshop.features.login.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.login.data.state.LoginState
import com.iti.pocketshop.features.login.domain.mapper.LoginResult
import com.iti.pocketshop.features.login.domain.usecase.ContinueAsGuestUseCase
import com.iti.pocketshop.features.login.domain.usecase.LoginWithEmailUseCase
import com.iti.pocketshop.features.login.domain.usecase.LoginWithGoogleUseCase
import com.iti.pocketshop.features.login.presentation.action.LoginAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            is LoginAction.LoginClicked -> login()

            is LoginAction.GoogleLoginClicked -> googleLogin()

            is LoginAction.ContinueAsGuestClicked -> continueAsGuest()


             is LoginAction.EmailChanged -> _state.update { it.copy(email = action.value, emailError = null) }

            is LoginAction.PasswordChanged -> _state.update { it.copy(password = action.value, passwordError = null) }

            is LoginAction.TogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
        }

    }

     private fun login() {
         viewModelScope.launch {
             val email = _state.value.email
             val password = _state.value.password

             if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                 _state.update { it.copy(emailError = "Invalid email format") }
                 return@launch
             }

             if (password.isBlank() || password.length < 6) {
                 _state.update { it.copy(passwordError = "Password must be at least 6 characters") }
                 return@launch
             }

             _state.update { it.copy(isLoading = true, generalError = null, passwordError = null, emailError = null) }

             val result = loginWithEmailUseCase(
                email = email,
                password = password
            )

             _state.update {
                when (result) {
                    is LoginResult.Success -> it.copy(isLoading = false, generalError = null, isLoginSuccessful = true)

                    is LoginResult.Error -> {
                         if (result.message.contains("password", ignoreCase = true)) {
                             it.copy(isLoading = false, passwordError = result.message)
                         } else if (result.message.contains("email", ignoreCase = true) || result.message.contains("user", ignoreCase = true)) {
                             it.copy(isLoading = false, emailError = result.message)
                         } else {
                             it.copy(isLoading = false, generalError = result.message)
                         }
                    }
                }
            }
        }
    }

    private fun googleLogin() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = loginWithGoogleUseCase()

            _state.update { currentState ->
                when (result) {
                    is LoginResult.Success -> currentState.copy(isLoading = false, isLoginSuccessful = true)
                    is LoginResult.Error -> currentState.copy(isLoading = false, generalError = result.message)
                }
            }
        }
    }

    private fun continueAsGuest() {

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = continueAsGuestUseCase()

            _state.update {
                when (result) {
                    is LoginResult.Success -> it.copy(isLoading = false, isLoginSuccessful = true)

                    is LoginResult.Error -> it.copy(isLoading = false, generalError = result.message)
                }
            }
        }
    }

}
