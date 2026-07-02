package com.iti.pocketshop.features.auth.forgetpassword.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: Boolean = false,
    val isSent: Boolean = false,
    val error: PocketDataError? = null,
)
