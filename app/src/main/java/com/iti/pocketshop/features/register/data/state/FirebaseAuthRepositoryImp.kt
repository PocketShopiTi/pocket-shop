package com.iti.pocketshop.features.register.data.state

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.iti.pocketshop.features.register.domain.repo.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            // .await() pauses the coroutine until the Firebase task completes
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    private fun mapFirebaseException(exception: Exception): Exception {
        return when (exception) {
            is FirebaseAuthWeakPasswordException ->
                Exception("Firebase rejected this password as too weak.")
            is FirebaseAuthInvalidCredentialsException ->
                Exception("Firebase rejected this email format.")
            is FirebaseAuthUserCollisionException ->
                Exception("An account with this email already exists.")
            else ->
                Exception(exception.message ?: "An unknown network or auth error occurred.")
        }
    }
}