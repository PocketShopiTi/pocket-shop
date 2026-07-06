package com.iti.pocketshop.network

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.net.UnknownHostException

suspend fun <T> safeFirebaseCall(block: suspend () -> T): PocketResult<T, PocketDataError.Auth> =
    try {
        PocketResult.Success(block())
    } catch (error: FirebaseNetworkException) {
        PocketResult.Error(PocketDataError.Auth.NO_INTERNET)
    } catch (error: UnknownHostException) {
        PocketResult.Error(PocketDataError.Auth.NO_INTERNET)
    } catch (error: FirebaseAuthInvalidCredentialsException) {
        PocketResult.Error(PocketDataError.Auth.INVALID_CREDENTIALS)
    } catch (error: FirebaseAuthUserCollisionException) {
        PocketResult.Error(PocketDataError.Auth.EMAIL_ALREADY_IN_USE)
    } catch (error: FirebaseAuthWeakPasswordException) {
        PocketResult.Error(PocketDataError.Auth.WEAK_PASSWORD)
    } catch (error: FirebaseAuthInvalidUserException) {
        PocketResult.Error(
            if (error.errorCode == "ERROR_USER_DISABLED") {
                PocketDataError.Auth.USER_DISABLED
            } else {
                PocketDataError.Auth.USER_NOT_FOUND
            }
        )
    } catch (error: FirebaseAuthException) {
        val mappedError = when (error.errorCode) {
            "ERROR_USER_DISABLED" -> PocketDataError.Auth.USER_DISABLED
            "ERROR_USER_NOT_FOUND" -> PocketDataError.Auth.USER_NOT_FOUND
            "ERROR_EMAIL_NOT_VERIFIED" -> PocketDataError.Auth.EMAIL_NOT_VERIFIED
            else -> PocketDataError.Auth.UNKNOWN
        }
        PocketResult.Error(mappedError)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        currentCoroutineContext().ensureActive()
        Log.e("FirebaseSafeCall", error.localizedMessage, error)
        PocketResult.Error(PocketDataError.Auth.UNKNOWN)
    }
