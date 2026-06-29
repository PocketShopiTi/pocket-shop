package com.iti.pocketshop.features.register.presentation.action


sealed interface RegisterAction {
    data class FullNameChanged(val fullName: String) : RegisterAction
    data class EmailChanged(val email: String) : RegisterAction
    data class PasswordChanged(val password: String) : RegisterAction
    data class ConfirmPasswordChanged(val password: String) : RegisterAction
    data object RegisterClicked : RegisterAction
    data object ClearError : RegisterAction
}