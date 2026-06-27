package com.iti.pocketshop.features.login.presentation.action

sealed class LoginAction {
    data class EmailChanged(val value: String) : LoginAction()
    data class PasswordChanged(val value: String) : LoginAction()
    object TogglePasswordVisibility : LoginAction()
    object LoginClicked : LoginAction()
    object GoogleLoginClicked : LoginAction()
    object ContinueAsGuestClicked : LoginAction()
    object ForgotPasswordClicked : LoginAction()
    object CreateAccountClicked : LoginAction()
}