package com.iti.pocketshop.features.auth.register.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RegisterEvent {
    data object NavigateToVerification : RegisterEvent
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegisterEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.FirstNameChanged -> _state.update {
                it.copy(
                    firstNameInput = action.firstName,
                    firstNameError = false,
                )
            }

            is RegisterAction.LastNameChanged -> _state.update {
                it.copy(lastNameInput = action.lastName, lastNameError = false)
            }

            is RegisterAction.EmailChanged -> _state.update {
                it.copy(emailInput = action.email, emailError = false)
            }

            is RegisterAction.PasswordChanged -> _state.update {
                it.copy(passwordInput = action.password, passwordError = false)
            }

            is RegisterAction.ConfirmPasswordChanged -> _state.update {
                it.copy(
                    confirmPasswordInput = action.password,
                    confirmPasswordError = false,
                )
            }

            RegisterAction.RegisterClicked -> registerUser()
            RegisterAction.ClearError -> _state.update { it.copy(
                firstNameError = false,
                lastNameError = false,
                emailError = false,
                passwordError = false,
                confirmPasswordError = false,
            ) }
        }
    }

    private fun registerUser() {
        val current = _state.value
        val firstNameError = current.firstNameInput.isBlank()
        val lastNameError = current.lastNameInput.isBlank()
        val emailError = current.emailInput.isBlank() ||
                !Patterns.EMAIL_ADDRESS.matcher(current.emailInput.trim()).matches()
        val passwordError = current.passwordInput.length < 6
        val confirmPasswordError = current.passwordInput != current.confirmPasswordInput

        _state.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
            )
        }
        if (firstNameError || lastNameError || emailError || passwordError || confirmPasswordError) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (
                val result = registerUserUseCase(
                    AuthData(
                        email = current.emailInput,
                        password = current.passwordInput,
                        firstName = current.firstNameInput,
                        lastName = current.lastNameInput,
                    )
                )
            ) {
                is PocketResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(RegisterEvent.NavigateToVerification)
                }

                is PocketResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    ErrorDialogController.sendEvent(result.error)
                }
            }
        }
    }
}
