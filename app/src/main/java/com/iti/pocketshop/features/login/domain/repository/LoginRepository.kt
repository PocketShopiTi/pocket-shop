package com.iti.pocketshop.features.login.domain.repository

import com.iti.pocketshop.features.login.domain.mapper.LoginResult

interface LoginRepository {
    suspend fun loginWithEmail(email: String, password: String): LoginResult
    suspend fun loginWithGoogle(): LoginResult
    suspend fun continueAsGuest(): LoginResult
}