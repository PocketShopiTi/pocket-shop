package com.iti.pocketshop.features.register.data.state

data class RegisterState(
    val fullNameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isLoading: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val success: Boolean = false,
    val generalError: String? = null
)