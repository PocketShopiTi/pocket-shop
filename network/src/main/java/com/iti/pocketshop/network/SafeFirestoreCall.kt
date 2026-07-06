package com.iti.pocketshop.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend fun <T> safeFirestoreCall(
    block: suspend () -> T,
): PocketResult<T, PocketDataError.Auth> =
    try {
        PocketResult.Success(block())
    } catch (error: FirebaseFirestoreException) {
        val mappedError = when (error.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> PocketDataError.Auth.UnAuthorized

            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> PocketDataError.Auth.NO_INTERNET

            else -> PocketDataError.Auth.UNKNOWN
        }
        PocketResult.Error(mappedError)
    } catch (error: Exception) {
        currentCoroutineContext().ensureActive()
        Log.e("FirestoreSafeCall", error.localizedMessage, error)
        PocketResult.Error(PocketDataError.Auth.UNKNOWN)
    }
