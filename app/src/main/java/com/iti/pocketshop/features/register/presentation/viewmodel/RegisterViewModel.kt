package com.iti.pocketshop.features.register.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.register.presentation.state.RegisterState
import com.iti.pocketshop.features.register.presentation.action.RegisterAction
import com.iti.pocketshop.features.register.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.FullNameChanged -> {
                _state.update {
                    it.copy(
                        fullNameInput = action.fullName,
                        isFullNameError = false,
                    )
                }
            }

            is RegisterAction.EmailChanged -> {
                _state.update {
                    it.copy(
                        emailInput = action.email,
                        isEmailError = false,
                    )
                }
            }

            is RegisterAction.PasswordChanged -> {
                _state.update {
                    it.copy(
                        passwordInput = action.password,
                        isPasswordError = false,
                    )
                }
            }

            is RegisterAction.ConfirmPasswordChanged -> {
                _state.update {
                    it.copy(
                        confirmPasswordInput = action.password,
                        isConfirmPasswordError = false,
                    )
                }
            }

            is RegisterAction.RegisterClicked -> {
                registerUser()
            }

            is RegisterAction.ClearError -> {
                _state.update { it.copy(
                    isFullNameError = false,
                    isEmailError = false,
                    isPasswordError = false,
                    isConfirmPasswordError = false,
                ) }
            }
        }
    }

    private fun registerUser() {
        val currentState = _state.value
        val fullName = currentState.fullNameInput
        val email = currentState.emailInput
        val password = currentState.passwordInput
        val confirmPassword = currentState.confirmPasswordInput

        var hasError = false
        if (fullName.isBlank()) {
            _state.update { it.copy(isFullNameError = true) }
            hasError = true
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _state.update { it.copy(isEmailError = true) }
            hasError = true
        }

        if (password.length < 6) {
            _state.update { it.copy(isPasswordError = true) }
            hasError = true
        }

        if (password != confirmPassword) {
            _state.update { it.copy(isConfirmPasswordError = true) }
            hasError = true
        }

        if (hasError) return

        _state.update {
            it.copy(
                isLoading = true,
                isFullNameError = false,
                isEmailError = false,
                isPasswordError = false,
                isConfirmPasswordError = false,
            )
        }

        viewModelScope.launch {
            val result = registerUserUseCase(email, password)
            result.onSuccess {
                try {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    val user = firebaseAuth.currentUser
                    if (user != null && fullName.isNotBlank()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()
                        user.updateProfile(profileUpdates).await()
                    }
                } catch (e: Exception) {
                    // Fail silently or log error for profile display name update, authentication itself was successful
                }
                _state.update { it.copy(isLoading = false, success = true) }
            }.onError { error ->
                _state.update { it.copy(isLoading = false) }
                ErrorDialogController.sendEvent(error)
            }
        }
    }
}