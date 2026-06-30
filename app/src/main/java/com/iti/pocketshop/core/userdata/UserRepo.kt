package com.iti.pocketshop.core.userdata

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    val currentUser: FirebaseUser?

    val isSignedIn: Boolean

    fun observeAuthState(): Flow<FirebaseUser?>

    fun signOut()

    suspend fun isUserLoggedIn(): Boolean
}