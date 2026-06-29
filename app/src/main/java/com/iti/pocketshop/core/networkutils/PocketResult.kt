package com.iti.pocketshop.core.networkutils


sealed interface PocketResult<out D, out E : Error> {
    data class Success<out D>(val data: D) : PocketResult<D, Nothing>
    data class Error<out E : com.iti.pocketshop.core.networkutils.Error>(val error: E) :
        PocketResult<Nothing, E>
}

inline fun <T, E : Error, R> PocketResult<T, E>.map(map: (T) -> R): PocketResult<R, E> {
    return when (this) {
        is PocketResult.Error -> PocketResult.Error(error)
        is PocketResult.Success -> PocketResult.Success(map(data))
    }
}

fun <T, E : Error> PocketResult<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

inline fun <T, E : Error> PocketResult<T, E>.onSuccess(action: (T) -> Unit): PocketResult<T, E> {
    return when (this) {
        is PocketResult.Error -> this
        is PocketResult.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E : Error> PocketResult<T, E>.onError(action: (E) -> Unit): PocketResult<T, E> {
    return when (this) {
        is PocketResult.Error -> {
            action(error)
            this
        }

        is PocketResult.Success -> this
    }
}

typealias EmptyResult<E> = PocketResult<Unit, E>