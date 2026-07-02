package com.iti.pocketshop.features.auth.login.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser

interface LoginRepository {
    suspend fun loginWithEmail(
        email: String,
        password: String
    ): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun loginWithGoogle(idToken: String): PocketResult<AuthUser, PocketDataError.Auth>
    suspend fun continueAsGuest(): PocketResult<AuthUser, PocketDataError.Auth>
}
