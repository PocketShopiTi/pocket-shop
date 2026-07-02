package com.iti.pocketshop.features.register.data

import com.google.firebase.auth.FirebaseAuth
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.toPocketFirebaseError
import com.iti.pocketshop.features.register.domain.repo.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String): PocketResult<Unit, PocketDataError> {
        return try {
            // .await() pauses the coroutine until the Firebase task completes
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            PocketResult.Success(Unit)
        } catch (e: Exception) {
            PocketResult.Error(e.toPocketFirebaseError())
        }
    }

}