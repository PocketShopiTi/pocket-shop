package com.iti.pocketshop.features.login.domain.model

sealed class LoginResult {
    data class Success(val user:  User) : LoginResult()
    data class Error(val error: LoginError) : LoginResult()
}