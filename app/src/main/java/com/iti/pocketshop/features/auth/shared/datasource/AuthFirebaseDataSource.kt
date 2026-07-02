package com.iti.pocketshop.features.auth.shared.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser

interface AuthFirebaseDataSource {
    fun currentUser(): AuthUser?

    suspend fun createUser(data: AuthData): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun signInWithGoogle(idToken: String): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun signInAnonymously(): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun reloadCurrentUser(): PocketResult<AuthUser, PocketDataError.Auth>

    suspend fun sendVerificationEmail(): PocketResult<Unit, PocketDataError.Auth>

    suspend fun sendPasswordReset(email: String): PocketResult<Unit, PocketDataError.Auth>

    fun signOut()
}