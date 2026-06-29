package com.iti.pocketshop.core.networkutils

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloNetworkException
import com.apollographql.apollo.exception.ApolloHttpException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "ApolloSafeCall"

suspend fun <D : Operation.Data> ApolloCall<D>.safeCall(): PocketResult<D, PocketDataError.Remote> {
    val response = try {
        execute()
    } catch (e: ApolloHttpException) {
        Log.e(TAG, "safeCall: ${e.localizedMessage}", e)
        return apolloHttpToResult(e)
    } catch (e: ApolloNetworkException) {
        Log.e(TAG, "safeCall: ${e.localizedMessage}", e)
        return when (e.cause) {
            is SocketTimeoutException -> PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
            is UnknownHostException -> PocketResult.Error(PocketDataError.Remote.NO_INTERNET)
            else -> PocketResult.Error(PocketDataError.Remote.NO_INTERNET)
        }
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Log.e(TAG, "safeCall: ${e.localizedMessage}", e)
        return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
    }

    return response.toResult()
}

fun <D : Operation.Data> ApolloResponse<D>.toResult(): PocketResult<D, PocketDataError.Remote> {
    if (hasErrors()) {
        val errorCode = errors?.firstOrNull()?.extensions?.get("code") as? String
        return when (errorCode) {
            "TIMEOUT" -> PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
            "RATE_LIMITED" -> PocketResult.Error(PocketDataError.Remote.TOO_MANY_REQUESTS)
            else -> PocketResult.Error(PocketDataError.Remote.SERVER)
        }
    }

    val d = data ?: return PocketResult.Error(PocketDataError.Remote.SERIALIZATION)
    return PocketResult.Success(d)
}

private fun apolloHttpToResult(e: ApolloHttpException): PocketResult<Nothing, PocketDataError.Remote> {
    return when (e.statusCode) {
        408 -> PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
        429 -> PocketResult.Error(PocketDataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> PocketResult.Error(PocketDataError.Remote.SERVER)
        else -> PocketResult.Error(PocketDataError.Remote.UNKNOWN)
    }
}