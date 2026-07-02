package com.iti.pocketshop.features.auth.shared

import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import com.iti.pocketshop.features.auth.shared.datasource.AuthFirebaseDataSource
import javax.inject.Inject

class SharedAuthRepositoryImpl @Inject constructor(
    private val firebase: AuthFirebaseDataSource,
) : SharedAuthRepository {
    override fun currentUser(): AuthUser? = firebase.currentUser()

    override suspend fun reloadCurrentUser() = firebase.reloadCurrentUser()

    override suspend fun resendVerificationEmail() = firebase.sendVerificationEmail()

    override suspend fun sendPasswordReset(email: String) = firebase.sendPasswordReset(email)

    override fun signOut() = firebase.signOut()
}