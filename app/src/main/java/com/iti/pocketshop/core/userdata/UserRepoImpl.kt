package com.iti.pocketshop.core.userdata

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.components.SignInDialogController
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val auth: FirebaseAuth
) : UserRepo {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isSignedIn: Boolean
        get() = currentUser != null

    val isAnonymous: Boolean
        get() = currentUser?.isAnonymous ?: false

    override fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun signOut() {
        auth.signOut()
    }

    override suspend fun isUserNotAnonymous(): Boolean {
        if (isAnonymous) {
            SignInDialogController.sendEvent(true)
            return false
        }
        return true
    }
}