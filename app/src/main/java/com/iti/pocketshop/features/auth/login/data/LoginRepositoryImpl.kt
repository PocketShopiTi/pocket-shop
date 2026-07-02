package com.iti.pocketshop.features.auth.login.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.login.domain.repository.LoginRepository
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import com.iti.pocketshop.features.auth.shared.datasource.AuthFirebaseDataSource
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebase: AuthFirebaseDataSource,
) : LoginRepository {
    override suspend fun loginWithEmail(
        email: String,
        password: String,
    ): PocketResult<AuthUser, PocketDataError.Auth> =
        firebase.signInWithEmailAndPassword(email, password)

    override suspend fun loginWithGoogle(
        idToken: String,
    ): PocketResult<AuthUser, PocketDataError.Auth> = firebase.signInWithGoogle(idToken)

    override suspend fun continueAsGuest():
            PocketResult<AuthUser, PocketDataError.Auth> = firebase.signInAnonymously()
}
