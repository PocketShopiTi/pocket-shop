package com.iti.pocketshop.features.auth.register.presentation

sealed interface RegisterAction {
    data class FirstNameChanged(val firstName: String) : RegisterAction
    data class LastNameChanged(val lastName: String) : RegisterAction
    data class EmailChanged(val email: String) : RegisterAction
    data class PasswordChanged(val password: String) : RegisterAction
    data class ConfirmPasswordChanged(val password: String) : RegisterAction
    data object RegisterClicked : RegisterAction
    data object ClearError : RegisterAction
}
