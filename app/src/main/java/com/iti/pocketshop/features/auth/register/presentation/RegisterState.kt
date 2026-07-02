package com.iti.pocketshop.features.auth.register.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError

data class RegisterState(
    val firstNameInput: String = "",
    val lastNameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isLoading: Boolean = false,
    val firstNameError: Boolean = false,
    val lastNameError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val generalError: PocketDataError? = null,
)
