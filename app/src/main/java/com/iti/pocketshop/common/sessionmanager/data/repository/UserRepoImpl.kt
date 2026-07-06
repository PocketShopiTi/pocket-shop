package com.iti.pocketshop.common.sessionmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.common.sessionmanager.data.datasource.firebase.FirebaseUserDataSource
import com.iti.pocketshop.common.sessionmanager.data.toUserSession
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.common.sessionmanager.domain.repository.UserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebase: FirebaseUserDataSource,
) : UserRepo {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isSignedIn: Boolean
        get() = currentUser != null

    val isAnonymous: Boolean
        get() = currentUser?.isAnonymous ?: false

    override fun currentSession(): UserSession? =
        when (val user = firebase.currentUser()) {
            null -> null
            else -> user.toUserSession()
        }

    override fun observeSession(): Flow<UserSession?> =
        firebase.observeAuthState().map { it?.toUserSession() }

    override suspend fun signOut(): PocketResult<Unit, PocketDataError.Auth> {
        return if (isAnonymous) {
            PocketResult.Success(Unit)
        } else {
            firebase.signOut()
        }
    }

    override suspend fun isUserNotAnonymous(): Boolean {
        if (isAnonymous) {
            SignInDialogController.sendEvent(true)
            return false
        }
        return true
    }
}