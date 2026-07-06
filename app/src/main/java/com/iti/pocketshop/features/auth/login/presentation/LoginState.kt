package com.iti.pocketshop.features.auth.login.presentation

import com.iti.pocketshop.network.PocketDataError

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val generalError: PocketDataError? = null,
)
