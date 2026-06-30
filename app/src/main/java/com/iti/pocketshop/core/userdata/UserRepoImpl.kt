package com.iti.pocketshop.core.userdata

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val auth: FirebaseAuth
) : UserRepo {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isSignedIn: Boolean
        get() = currentUser != null

    override fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun getIdToken(forceRefresh: Boolean): String? {
        return currentUser?.getIdToken(forceRefresh)?.await()?.token
    }

    override fun signOut() {
        auth.signOut()
    }
}