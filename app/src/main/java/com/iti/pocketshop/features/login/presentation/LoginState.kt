package com.iti.pocketshop.features.login.presentation

import com.iti.pocketshop.features.login.domain.model.LoginError


data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: LoginError? = null,
    val passwordError: LoginError? = null,
    val generalError: LoginError? = null,
    val isLoginEnabled: Boolean = false,
    val isLoginSuccessful: Boolean = false
)