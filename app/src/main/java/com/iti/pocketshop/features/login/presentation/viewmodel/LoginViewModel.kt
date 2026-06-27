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

            is LoginAction.ForgotPasswordClicked -> forgotPassword()

            is LoginAction.CreateAccountClicked -> createAccount()

             is LoginAction.EmailChanged -> _state.update { it.copy(email = action.value, emailError = null) }

            is LoginAction.PasswordChanged -> _state.update { it.copy(password = action.value, passwordError = null) }

            is LoginAction.TogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
        }

    }

     private fun login() {
         viewModelScope.launch {
             _state.update { it.copy(isLoading = true, generalError = null, passwordError = null) }

             val result = loginWithEmailUseCase(
                email = _state.value.email,
                password = _state.value.password
            )

             _state.update {
                when (result) {
                    is LoginResult.Success -> it.copy(isLoading = false, generalError = null)

                    is LoginResult.Error -> {

                         if (result.message.contains("Password", ignoreCase = true))
                             it.copy(isLoading = false, passwordError = result.message)

                         else it.copy(isLoading = false, generalError = result.message)

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
                    is LoginResult.Success -> currentState.copy(isLoading = false)
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
                    is LoginResult.Success -> it.copy(isLoading = false)

                    is LoginResult.Error -> it.copy(isLoading = false, generalError = result.message)
                }
            }
        }
    }

    private fun forgotPassword() {
        // Nav
     }

    private fun createAccount() {
        // Nav
     }
}
