package com.iti.pocketshop.core.networkutils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.iti.pocketshop.R

private const val TAG = "PocketDataError"

sealed interface PocketDataError : Error {
    enum class Remote : PocketDataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN,
        EMPTY_RESULT
    }

    enum class Auth : PocketDataError {
        INVALID_CREDENTIALS,
        USER_NOT_FOUND,
        EMAIL_ALREADY_IN_USE,
        WEAK_PASSWORD,
        ACCOUNT_DISABLED,
        EMAIL_NOT_VERIFIED,
        OPERATION_NOT_ALLOWED,
        NETWORK_ERROR,
        UnAuthorized,
        NO_INTERNET,
        USER_DISABLED,
        INVALID_EMAIL,
        UNKNOWN,
        USER_DISABLED,
        TOKEN_NOT_VALID,
    }

    enum class Firestore : PocketDataError {
        PERMISSION_DENIED,
        NOT_FOUND,
        ALREADY_EXISTS,
        UNAVAILABLE,
        QUOTA_EXCEEDED,
        DATA_LOSS,
        CANCELLED
    }
}

fun PocketDataError.toUserMessage(context: Context): String = when (this) {
    PocketDataError.Remote.REQUEST_TIMEOUT -> context.getString(R.string.request_timed_out)
    PocketDataError.Remote.TOO_MANY_REQUESTS -> context.getString(R.string.too_many_requests)
    PocketDataError.Remote.NO_INTERNET -> context.getString(R.string.no_internet_connection)
    PocketDataError.Remote.SERVER -> context.getString(R.string.server_error)
    PocketDataError.Remote.SERIALIZATION -> context.getString(R.string.failed_to_process_response)
    PocketDataError.Remote.UNKNOWN -> context.getString(R.string.something_went_wrong)
    PocketDataError.Remote.EMPTY_RESULT -> context.getString(R.string.no_results_found)

    // Auth
    PocketDataError.Auth.INVALID_EMAIL-> context.getString(R.string.auth_invalid_email)
    PocketDataError.Auth.INVALID_CREDENTIALS -> context.getString(R.string.auth_invalid_credentials)
    PocketDataError.Auth.USER_NOT_FOUND -> context.getString(R.string.auth_user_not_found)
    PocketDataError.Auth.EMAIL_ALREADY_IN_USE -> context.getString(R.string.auth_email_already_in_use)
    PocketDataError.Auth.WEAK_PASSWORD -> context.getString(R.string.auth_weak_password)
    PocketDataError.Auth.ACCOUNT_DISABLED -> context.getString(R.string.auth_account_disabled)
    PocketDataError.Auth.EMAIL_NOT_VERIFIED -> context.getString(R.string.auth_email_not_verified)
    PocketDataError.Auth.OPERATION_NOT_ALLOWED -> context.getString(R.string.auth_operation_not_allowed)
    PocketDataError.Auth.NETWORK_ERROR -> context.getString(R.string.no_internet_connection)
    PocketDataError.Auth.UnAuthorized -> context.getString(R.string.error_unauthorized)
    PocketDataError.Auth.NO_INTERNET -> context.getString(R.string.error_network)
    PocketDataError.Auth.USER_DISABLED -> context.getString(R.string.error_user_disabled)
    PocketDataError.Auth.UNKNOWN -> context.getString(R.string.error_unknown)

    // Firestore
    PocketDataError.Firestore.PERMISSION_DENIED -> context.getString(R.string.firestore_permission_denied)
    PocketDataError.Firestore.NOT_FOUND -> context.getString(R.string.firestore_not_found)
    PocketDataError.Firestore.ALREADY_EXISTS -> context.getString(R.string.firestore_already_exists)
    PocketDataError.Firestore.UNAVAILABLE -> context.getString(R.string.firestore_unavailable)
    PocketDataError.Firestore.QUOTA_EXCEEDED -> context.getString(R.string.firestore_quota_exceeded)
    PocketDataError.Firestore.DATA_LOSS -> context.getString(R.string.firestore_data_loss)
    PocketDataError.Firestore.CANCELLED -> context.getString(R.string.firestore_cancelled)
    PocketDataError.Auth.TOKEN_NOT_VALID -> context.getString(R.string.error_token_not_valid)

}

// for firebase errors
fun Throwable.toPocketFirebaseError(): PocketDataError {
    Log.e(TAG, "toPocketFirebaseError: ", this)
    return when (this) {
        is FirebaseAuthException -> toAuthError()
        is FirebaseFirestoreException -> toFirestoreError()
        is FirebaseNetworkException -> PocketDataError.Remote.NO_INTERNET
        else -> PocketDataError.Remote.UNKNOWN
    }
}

private fun FirebaseAuthException.toAuthError(): PocketDataError.Auth {
    return when (errorCode) {
        "ERROR_WRONG_PASSWORD",
        "ERROR_INVALID_CREDENTIAL",
        "ERROR_INVALID_EMAIL" -> PocketDataError.Auth.INVALID_CREDENTIALS

        "ERROR_USER_NOT_FOUND" -> PocketDataError.Auth.USER_NOT_FOUND
        "ERROR_EMAIL_ALREADY_IN_USE" -> PocketDataError.Auth.EMAIL_ALREADY_IN_USE
        "ERROR_WEAK_PASSWORD" -> PocketDataError.Auth.WEAK_PASSWORD
        "ERROR_USER_DISABLED" -> PocketDataError.Auth.ACCOUNT_DISABLED
        "ERROR_OPERATION_NOT_ALLOWED" -> PocketDataError.Auth.OPERATION_NOT_ALLOWED
        "ERROR_NETWORK_REQUEST_FAILED" -> PocketDataError.Auth.NETWORK_ERROR
        else -> PocketDataError.Auth.INVALID_CREDENTIALS
    }
}

private fun FirebaseFirestoreException.toFirestoreError(): PocketDataError.Firestore {
    return when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> PocketDataError.Firestore.PERMISSION_DENIED
        FirebaseFirestoreException.Code.NOT_FOUND -> PocketDataError.Firestore.NOT_FOUND
        FirebaseFirestoreException.Code.ALREADY_EXISTS -> PocketDataError.Firestore.ALREADY_EXISTS
        FirebaseFirestoreException.Code.UNAVAILABLE -> PocketDataError.Firestore.UNAVAILABLE
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> PocketDataError.Firestore.QUOTA_EXCEEDED
        FirebaseFirestoreException.Code.DATA_LOSS -> PocketDataError.Firestore.DATA_LOSS
        FirebaseFirestoreException.Code.CANCELLED -> PocketDataError.Firestore.CANCELLED
        else -> PocketDataError.Firestore.UNAVAILABLE
    }
}
