package com.iti.pocketshop.core.networkutils

import android.content.Context
import com.iti.pocketshop.R

sealed interface PocketDataError : Error {
    enum class Remote : PocketDataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }
}

fun PocketDataError.toUserMessage(context: Context): String = when (this) {
    PocketDataError.Remote.REQUEST_TIMEOUT -> context.getString(R.string.request_timed_out)
    PocketDataError.Remote.TOO_MANY_REQUESTS -> context.getString(R.string.too_many_requests)
    PocketDataError.Remote.NO_INTERNET -> context.getString(R.string.no_internet_connection)
    PocketDataError.Remote.SERVER -> context.getString(R.string.server_error)
    PocketDataError.Remote.SERIALIZATION -> context.getString(R.string.failed_to_process_response)
    PocketDataError.Remote.UNKNOWN -> context.getString(R.string.something_went_wrong)
}