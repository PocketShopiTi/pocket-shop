package com.iti.pocketshop.features.login.presentation.action

sealed class LoginAction {
    data class EmailChanged(val value: String) : LoginAction()
    data class PasswordChanged(val value: String) : LoginAction()
    object TogglePasswordVisibility : LoginAction()
    object LoginClicked : LoginAction()
    data class GoogleLoginSubmitted(val idToken: String) : LoginAction()
    object ContinueAsGuestClicked : LoginAction()
}