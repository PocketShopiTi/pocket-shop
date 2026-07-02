package com.iti.pocketshop.common.sessionmanager.data.datasource.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeFirebaseCall
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseUserDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : FirebaseUserDataSource {
    override fun currentUser(): FirebaseUser? = auth.currentUser

    override fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signOut(): PocketResult<Unit, PocketDataError.Auth> =
        safeFirebaseCall { auth.signOut() }
}