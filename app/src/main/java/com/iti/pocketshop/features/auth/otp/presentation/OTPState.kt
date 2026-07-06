package com.iti.pocketshop.features.auth.otp.presentation

import com.iti.pocketshop.network.PocketDataError

data class EmailVerificationState(
    val email: String = "",
    val isChecking: Boolean = false,
    val resendCooldown: Int = 0,
    val isNotVerifiedYet: Boolean = false,
    val resendSucceeded: Boolean = false,
    val error: PocketDataError? = null,
)
