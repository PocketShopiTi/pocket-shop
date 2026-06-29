package com.iti.pocketshop.features.login.domain.repository

import com.iti.pocketshop.features.login.domain.model.LoginResult

interface LoginRepository {
    suspend fun loginWithEmail(email: String, password: String): LoginResult
    suspend fun loginWithGoogle(idToken: String): LoginResult
    suspend fun continueAsGuest(): LoginResult
}