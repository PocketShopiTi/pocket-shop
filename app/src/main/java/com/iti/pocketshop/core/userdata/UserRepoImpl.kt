package com.iti.pocketshop.core.userdata

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.core.tokenmanager.data.datasource.local.CustomerTokenStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val tokenStore: CustomerTokenStore,
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
        tokenStore.clear()
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
