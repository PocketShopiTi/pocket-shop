package com.iti.pocketshop.core.networkutils

import android.util.Log
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.UnknownHostException

private const val TAG = "SafeRestCall"

suspend fun <T> safeRestCall(block: suspend () -> T): PocketResult<T, PocketDataError.Remote> =
    try {
        PocketResult.Success(block())
    } catch (e: HttpRequestTimeoutException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
    } catch (e: ConnectTimeoutException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SocketTimeoutException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
    } catch (e: ResponseException) {
        Log.e(TAG, "safeRestCall: HTTP ${e.response.status.value}")
        httpToResult(e.response.status.value)
    } catch (e: JsonConvertException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.SERIALIZATION)
    } catch (e: SerializationException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.SERIALIZATION)
    } catch (e: UnknownHostException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.NO_INTERNET)
    } catch (e: IOException) {
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.NO_INTERNET)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Log.e(TAG, "safeRestCall: ${e.localizedMessage}")
        PocketResult.Error(PocketDataError.Remote.UNKNOWN)
    }

private fun httpToResult(statusCode: Int): PocketResult<Nothing, PocketDataError.Remote> =
    when (statusCode) {
        408 -> PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)
        429 -> PocketResult.Error(PocketDataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> PocketResult.Error(PocketDataError.Remote.SERVER)
        else -> PocketResult.Error(PocketDataError.Remote.UNKNOWN)
    }
