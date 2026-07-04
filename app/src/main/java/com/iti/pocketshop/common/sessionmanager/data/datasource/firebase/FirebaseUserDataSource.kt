package com.iti.pocketshop.common.sessionmanager.data.datasource.firebase

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import kotlinx.coroutines.flow.Flow

interface FirebaseUserDataSource {
    fun currentUser(): FirebaseUser?

    fun observeAuthState(): Flow<FirebaseUser?>

    suspend fun signOut(): PocketResult<Unit, PocketDataError.Auth>
}