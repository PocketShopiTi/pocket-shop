package com.iti.pocketshop.features.auth.login.presentation

sealed interface LoginAction {
    data class EmailChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    object TogglePasswordVisibility : LoginAction
    object LoginClicked : LoginAction
    data class GoogleLoginSubmitted(val idToken: String) : LoginAction
    object GoogleSignInFailed : LoginAction
    object ContinueAsGuestClicked : LoginAction
}