package com.iti.pocketshop.features.auth.shared.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeFirebaseCall
import com.iti.pocketshop.features.auth.register.data.toDomain
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthFirebaseDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthFirebaseDataSource {

    override fun currentUser(): AuthUser? = auth.currentUser?.toDomain()

    override suspend fun createUser(
        data: AuthData,
    ): PocketResult<AuthUser, PocketDataError.Auth> =
        safeFirebaseCall {
            val user = auth.createUserWithEmailAndPassword(data.email, data.password)
                .await().user
                ?: error("Firebase user is missing after registration")

            user.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(data.fullName).build()
            ).await()

            user.toDomain()
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): PocketResult<AuthUser, PocketDataError.Auth> =
        safeFirebaseCall {
            auth.signInWithEmailAndPassword(email, password).await().user?.toDomain()
                ?: error("Firebase user is missing after login")
        }

    override suspend fun signInWithGoogle(
        idToken: String,
    ): PocketResult<AuthUser, PocketDataError.Auth> =
        safeFirebaseCall {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await().user?.toDomain()
                ?: error("Firebase user is missing after Google login")
        }

    override suspend fun signInAnonymously(): PocketResult<AuthUser, PocketDataError.Auth> =
        safeFirebaseCall {
            auth.signInAnonymously().await().user?.toDomain()
                ?: error("Firebase user is missing after guest login")
        }

    override suspend fun reloadCurrentUser(): PocketResult<AuthUser, PocketDataError.Auth> =
        safeFirebaseCall {
            val user = auth.currentUser ?: error("No authenticated Firebase user")
            user.reload().await()
            auth.currentUser?.toDomain() ?: error("Firebase user disappeared after reload")
        }

    override suspend fun sendVerificationEmail(): PocketResult<Unit, PocketDataError.Auth> =
        safeFirebaseCall {
            val user = auth.currentUser ?: error("No authenticated Firebase user")
            user.sendEmailVerification().await()
        }

    override suspend fun sendPasswordReset(
        email: String,
    ): PocketResult<Unit, PocketDataError.Auth> =
        safeFirebaseCall {
            auth.sendPasswordResetEmail(email).await()
        }

    override fun signOut() = auth.signOut()

}
