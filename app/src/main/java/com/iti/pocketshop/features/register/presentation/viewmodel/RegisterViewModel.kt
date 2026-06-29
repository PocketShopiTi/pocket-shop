package com.iti.pocketshop.features.register.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.features.register.data.state.RegisterState
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

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.FullNameChanged -> {
                _state.update { it.copy(fullNameInput = action.fullName, fullNameError = null, generalError = null) }
            }
            is RegisterAction.EmailChanged -> {
                _state.update { it.copy(emailInput = action.email, emailError = null, generalError = null) }
            }
            is RegisterAction.PasswordChanged -> {
                _state.update { it.copy(passwordInput = action.password, passwordError = null, generalError = null) }
            }
            is RegisterAction.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPasswordInput = action.password, confirmPasswordError = null, generalError = null) }
            }
            is RegisterAction.RegisterClicked -> {
                registerUser()
            }
            is RegisterAction.ClearError -> {
                _state.update { it.copy(generalError = null) }
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
            _state.update { it.copy(fullNameError = "Name cannot be empty") }
            hasError = true
        }

        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Email cannot be empty") }
            hasError = true
        } else if (!email.trim().contains("@") || !email.trim().contains(".")) {
            _state.update { it.copy(emailError = "Please enter a valid email address") }
            hasError = true
        }

        if (password.length < 8) {
            _state.update { it.copy(passwordError = "Password must be at least 8 characters") }
            hasError = true
        }

        if (password != confirmPassword) {
            _state.update { it.copy(confirmPasswordError = "Passwords do not match") }
            hasError = true
        }

        if (hasError) return

        _state.update { it.copy(isLoading = true, generalError = null) }

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
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, generalError = error.message) }
            }
        }
    }
}