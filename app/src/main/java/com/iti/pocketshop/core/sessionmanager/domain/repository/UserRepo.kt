package com.iti.pocketshop.core.sessionmanager.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.sessionmanager.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    val currentUser: FirebaseUser?

    val isSignedIn: Boolean

    fun currentSession(): UserSession?

    fun observeSession(): Flow<UserSession?>

    suspend fun signOut(): PocketResult<Unit, PocketDataError.Auth>

    suspend fun isUserNotAnonymous(): Boolean
}