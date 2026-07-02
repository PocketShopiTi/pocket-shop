package com.iti.pocketshop.features.address.data.datasource

import android.util.Log
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.data.model.GoogleAutocompleteResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeocodeResponse
import com.iti.pocketshop.features.address.data.model.GooglePlaceDetailsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class AddressLocationRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
) : AddressLocationRemoteDataSource {

    override suspend fun searchSuggestions(
        query: String,
        apiKey: String,
    ): PocketResult<GoogleAutocompleteResponse, PocketDataError.Remote> {
        Log.d(TAG, "searchSuggestions()")
        Log.d(TAG, "Query = $query")

        return fetch(
            url = AUTOCOMPLETE_URL,
            apiKey = apiKey,
        ) {
            parameter("input", query)
            parameter("types", "address")
        }
    }

    override suspend fun resolveSuggestion(
        placeId: String,
        apiKey: String,
    ): PocketResult<GooglePlaceDetailsResponse, PocketDataError.Remote> {

        Log.d(TAG, "resolveSuggestion()")
        Log.d(TAG, "PlaceId = $placeId")

        return fetch(
            url = PLACE_DETAILS_URL,
            apiKey = apiKey,
        ) {
            parameter("place_id", placeId)
            parameter("fields", "formatted_address,address_components,geometry")
        }
    }

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String,
    ): PocketResult<GoogleGeocodeResponse, PocketDataError.Remote> {

        Log.d(TAG, "reverseGeocode()")
        Log.d(TAG, "LatLng = $latitude,$longitude")

        return fetch(
            url = GEOCODE_URL,
            apiKey = apiKey,
        ) {
            parameter("latlng", "$latitude,$longitude")
        }
    }

    private suspend inline fun <reified T : Any> fetch(
        url: String,
        apiKey: String,
        crossinline configure: HttpRequestBuilder.() -> Unit,
    ): PocketResult<T, PocketDataError.Remote> {

        return withContext(Dispatchers.IO) {
            try {

                Log.d(TAG, "===================================")
                Log.d(TAG, "URL      : $url")
                Log.d(TAG, "API Key  : ${apiKey.take(10)}...")
                Log.d(TAG, "Sending request...")

                val response = httpClient.get(url) {
                    parameter("key", apiKey)
                    configure()
                }

                Log.d(TAG, "Response Status : ${response.status}")
                Log.d(TAG, "Status Code     : ${response.status.value}")

                response.toResult()

            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Socket timeout", e)
                PocketResult.Error(PocketDataError.Remote.REQUEST_TIMEOUT)

            } catch (e: UnknownHostException) {
                Log.e(TAG, "No internet", e)
                PocketResult.Error(PocketDataError.Remote.NO_INTERNET)

            } catch (e: Exception) {
                Log.e(TAG, "Google request failed", e)
                PocketResult.Error(PocketDataError.Remote.UNKNOWN)
            }
        }
    }

    private suspend inline fun <reified T : Any> HttpResponse.toResult():
            PocketResult<T, PocketDataError.Remote> {

        Log.d(TAG, "Parsing response...")

        if (!status.isSuccess()) {

            Log.e(
                TAG,
                "HTTP Error -> code=${status.value}, description=$status"
            )

            return PocketResult.Error(status.toRemoteError())
        }

        return try {

            val body = body<T>()

            Log.d(TAG, "Response parsed successfully.")
            Log.d(TAG, "Parsed object = $body")

            PocketResult.Success(body)

        } catch (e: Exception) {

            Log.e(TAG, "Serialization error", e)

            PocketResult.Error(PocketDataError.Remote.SERIALIZATION)
        }
    }

    private fun HttpStatusCode.toRemoteError(): PocketDataError.Remote {
        return when (value) {
            408 -> PocketDataError.Remote.REQUEST_TIMEOUT
            429 -> PocketDataError.Remote.TOO_MANY_REQUESTS
            in 500..599 -> PocketDataError.Remote.SERVER
            else -> PocketDataError.Remote.UNKNOWN
        }
    }

    private companion object {
        const val TAG = "AddressLocationRemote"

        const val AUTOCOMPLETE_URL =
            "https://maps.googleapis.com/maps/api/place/autocomplete/json"

        const val PLACE_DETAILS_URL =
            "https://maps.googleapis.com/maps/api/place/details/json"

        const val GEOCODE_URL =
            "https://maps.googleapis.com/maps/api/geocode/json"
    }
}