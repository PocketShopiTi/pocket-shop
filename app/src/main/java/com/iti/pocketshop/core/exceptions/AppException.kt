package com.iti.pocketshop.core.exceptions

sealed class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NetworkError(cause: Throwable? = null) :
        AppException("No internet connection", cause)

    class ServerError(message: String = "Server error", cause: Throwable? = null) :
        AppException(message, cause)

    class Unknown(message: String = "Something went wrong", cause: Throwable? = null) :
        AppException(message, cause)
}