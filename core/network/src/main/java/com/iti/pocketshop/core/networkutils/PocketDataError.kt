package com.iti.pocketshop.core.networkutils

sealed interface PocketDataError : Error {
    enum class Validation : PocketDataError {
        EMPTY_CUSTOMER_NAME,
        EMPTY_TITLE,
        EMPTY_BODY,
        INVALID_RATING,
    }
    enum class Remote : PocketDataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN,
        EMPTY_RESULT,
        BAD_REQUEST,
        INVALID_COUPON,
        ADDRESS_ERROR,
    }

    data class CustomServerMessage(val message: String) : PocketDataError

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
        INVALID_EMAIL,
        UNKNOWN,
        USER_DISABLED,
        TOKEN_NOT_VALID,
    }

    enum class Payment : PocketDataError {
        CANCELED,
        NO_FUNDS,
        REJECTED,
        EXPIRED ,
        INVALID_CARD,
        FAILED
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
