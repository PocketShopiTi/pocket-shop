package com.iti.pocketshop.features.register.presentation.state

data class RegisterState(
    val fullNameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isLoading: Boolean = false,
    val isFullNameError: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isConfirmPasswordError: Boolean = false,
    val success: Boolean = false,
)