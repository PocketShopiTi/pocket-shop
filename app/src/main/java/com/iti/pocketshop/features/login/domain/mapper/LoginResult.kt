package com.iti.pocketshop.features.login.domain.mapper

sealed class LoginResult {
    data class Success(val user:  User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}