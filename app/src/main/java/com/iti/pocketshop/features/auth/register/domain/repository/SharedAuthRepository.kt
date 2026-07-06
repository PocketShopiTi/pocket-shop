package com.iti.pocketshop.features.auth.register.domain.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser

interface SharedAuthRepository {
    suspend fun reloadCurrentUser(): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun resendVerificationEmail(): PocketResult<Unit, PocketDataError.Auth>

    suspend fun sendPasswordReset(email: String): PocketResult<Unit, PocketDataError.Auth>
}
