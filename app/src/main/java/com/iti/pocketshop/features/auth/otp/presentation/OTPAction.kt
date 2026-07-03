package com.iti.pocketshop.features.auth.otp.presentation

sealed interface EmailVerificationAction {
    data object CheckVerification : EmailVerificationAction
    data object ResendEmail : EmailVerificationAction
    data object BackToLogin : EmailVerificationAction
}
