package com.iti.pocketshop.features.auth.forgetpassword.presentation

sealed interface ForgotPasswordAction {
    data class EmailChanged(val value: String) : ForgotPasswordAction
    data object Submit : ForgotPasswordAction
}
