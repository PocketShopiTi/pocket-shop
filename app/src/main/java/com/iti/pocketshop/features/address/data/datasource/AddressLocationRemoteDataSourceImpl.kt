package com.iti.pocketshop.features.address.data.datasource

import android.content.Context
import android.location.Address as AndroidAddress
import android.location.Geocoder
import android.util.Log
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.address.data.model.GoogleAddressComponent
import com.iti.pocketshop.features.address.data.model.GoogleAddressResult
import com.iti.pocketshop.features.address.data.model.GoogleAutocompletePrediction
import com.iti.pocketshop.features.address.data.model.GoogleAutocompleteResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeocodeResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeometry
import com.iti.pocketshop.features.address.data.model.GoogleLatLng
import com.iti.pocketshop.features.address.data.model.GooglePlaceDetailsResponse
import com.iti.pocketshop.features.address.data.model.GoogleStructuredFormatting
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressLocationRemoteDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AddressLocationRemoteDataSource {

    private val geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }

    override suspend fun searchSuggestions(
        query: String,
        apiKey: String,
    ): PocketResult<GoogleAutocompleteResponse, PocketDataError.Remote> {
        Log.d(TAG, "searchSuggestions()")
        Log.d(TAG, "Query = $query")

        return withContext(Dispatchers.IO) {
            if (query.isBlank()) {
                return@withContext PocketResult.Success(
                    GoogleAutocompleteResponse(status = "ZERO_RESULTS"),
                )
            }

            try {
                val addresses = geocodeByName(query, SEARCH_RESULTS_LIMIT)
                val predictions = addresses.mapNotNull { it.toAutocompletePrediction() }
                PocketResult.Success(
                    GoogleAutocompleteResponse(
                        predictions = predictions,
                        status = if (predictions.isEmpty()) "ZERO_RESULTS" else "OK",
                    ),
                )
            } catch (e: Exception) {
                Log.e(TAG, "Geocoder search failed", e)
                PocketResult.Error(e.toRemoteError())
            }
        }
    }

    override suspend fun resolveSuggestion(
        placeId: String,
        apiKey: String,
    ): PocketResult<GooglePlaceDetailsResponse, PocketDataError.Remote> {
        Log.d(TAG, "resolveSuggestion()")
        Log.d(TAG, "PlaceId = $placeId")

        return withContext(Dispatchers.IO) {
            val coordinates = placeId.toCoordinatesOrNull()
                ?: return@withContext PocketResult.Error(PocketDataError.Remote.UNKNOWN)

            try {
                val address = geocodeByLocation(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude,
                ).firstOrNull()
                    ?: return@withContext PocketResult.Success(
                        GooglePlaceDetailsResponse(status = "ZERO_RESULTS"),
                    )

                PocketResult.Success(
                    GooglePlaceDetailsResponse(
                        result = address.toGoogleAddressResult(),
                        status = "OK",
                    ),
                )
            } catch (e: Exception) {
                Log.e(TAG, "Geocoder place resolution failed", e)
                PocketResult.Error(e.toRemoteError())
            }
        }
    }

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String,
    ): PocketResult<GoogleGeocodeResponse, PocketDataError.Remote> {
        Log.d(TAG, "reverseGeocode()")
        Log.d(TAG, "LatLng = $latitude,$longitude")

        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocodeByLocation(latitude, longitude)
                val results = addresses.mapNotNull { it.toGoogleAddressResult() }

                PocketResult.Success(
                    GoogleGeocodeResponse(
                        results = results,
                        status = if (results.isEmpty()) "ZERO_RESULTS" else "OK",
                    ),
                )
            } catch (e: Exception) {
                Log.e(TAG, "Geocoder reverse lookup failed", e)
                PocketResult.Error(e.toRemoteError())
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun geocodeByName(query: String, maxResults: Int): List<AndroidAddress> {
        return geocoder.getFromLocationName(query, maxResults).orEmpty()
    }

    @Suppress("DEPRECATION")
    private fun geocodeByLocation(latitude: Double, longitude: Double): List<AndroidAddress> {
        return geocoder.getFromLocation(latitude, longitude, SEARCH_RESULTS_LIMIT).orEmpty()
    }

    private fun String.toCoordinatesOrNull(): Coordinates? {
        val raw = removePrefix("geo:")
        val parts = raw.split(",")
        if (parts.size < 2) {
            return null
        }

        val latitude = parts[0].toDoubleOrNull() ?: return null
        val longitude = parts[1].toDoubleOrNull() ?: return null
        return Coordinates(latitude, longitude)
    }

    private fun AndroidAddress.toAutocompletePrediction(): GoogleAutocompletePrediction? {
        val primaryText = toPrimaryText()
        val secondaryText = toSecondaryText()
        val placeId = toPlaceId()
        if (placeId.isBlank() || primaryText.isBlank() && secondaryText.isBlank()) {
            return null
        }

        val description = listOf(primaryText, secondaryText)
            .filter { it.isNotBlank() }
            .joinToString(", ")
            .ifBlank { addressLineOrFallback() }

        return GoogleAutocompletePrediction(
            placeId = placeId,
            description = description,
            structuredFormatting = GoogleStructuredFormatting(
                mainText = primaryText.ifBlank { description },
                secondaryText = secondaryText,
            ),
        )
    }

    private fun AndroidAddress.toGoogleAddressResult(): GoogleAddressResult? {
        val latitudeValue = latitude
        val longitudeValue = longitude
        val formattedAddress = addressLineOrFallback()
        val componentMap = buildAddressComponents()

        return GoogleAddressResult(
            formattedAddress = formattedAddress,
            addressComponents = componentMap,
            geometry = GoogleGeometry(
                location = GoogleLatLng(
                    lat = latitudeValue,
                    lng = longitudeValue,
                ),
            ),
        )
    }

    private fun AndroidAddress.buildAddressComponents(): List<GoogleAddressComponent> {
        val city = locality.orEmpty()
            .ifBlank { subAdminArea.orEmpty() }
            .ifBlank { adminArea.orEmpty() }

        return buildList {
            subThoroughfare.orEmpty().takeIf { it.isNotBlank() }?.let {
                add(addressComponent(it, "street_number"))
            }
            thoroughfare.orEmpty().takeIf { it.isNotBlank() }?.let {
                add(addressComponent(it, "route"))
            }
            city.takeIf { it.isNotBlank() }?.let {
                add(addressComponent(it, "locality"))
            }
            subAdminArea.orEmpty().takeIf { it.isNotBlank() }?.let {
                add(addressComponent(it, "administrative_area_level_2"))
            }
            adminArea.orEmpty().takeIf { it.isNotBlank() }?.let {
                add(addressComponent(it, "administrative_area_level_1"))
            }
            countryName.orEmpty().takeIf { it.isNotBlank() }?.let {
                add(
                    GoogleAddressComponent(
                        longName = it,
                        shortName = countryCode.orEmpty().ifBlank { it },
                        types = listOf("country"),
                    ),
                )
            }
            postalCode.orEmpty().takeIf { it.isNotBlank() }?.let {
                add(addressComponent(it, "postal_code"))
            }
        }
    }

    private fun AndroidAddress.toPrimaryText(): String {
        val line = listOfNotNull(
            subThoroughfare?.takeIf { it.isNotBlank() },
            thoroughfare?.takeIf { it.isNotBlank() },
            featureName?.takeIf { it.isNotBlank() },
        ).joinToString(" ")

        if (line.isNotBlank()) {
            return line
        }

        return listOf(
            locality,
            subAdminArea,
            adminArea,
            countryName,
        ).filter { it.isNotBlank() }
            .joinToString(", ")
    }

    private fun AndroidAddress.toSecondaryText(): String {
        return listOf(
            locality,
            subAdminArea,
            adminArea,
            countryName,
        ).filter { it.isNotBlank() }
            .joinToString(", ")
    }

    private fun AndroidAddress.toPlaceId(): String {
        return "geo:$latitude,$longitude"
    }

    private fun AndroidAddress.addressLineOrFallback(): String {
        return getAddressLine(0)?.takeIf { it.isNotBlank() }
            ?: listOf(
                toPrimaryText(),
                toSecondaryText(),
            ).filter { it.isNotBlank() }
                .joinToString(", ")
    }

    private fun addressComponent(longName: String, type: String): GoogleAddressComponent {
        return GoogleAddressComponent(
            longName = longName,
            shortName = longName,
            types = listOf(type),
        )
    }

    private fun Exception.toRemoteError(): PocketDataError.Remote {
        return when (this) {
            is IOException -> PocketDataError.Remote.NO_INTERNET
            is IllegalArgumentException -> PocketDataError.Remote.UNKNOWN
            else -> PocketDataError.Remote.UNKNOWN
        }
    }

    private data class Coordinates(
        val latitude: Double,
        val longitude: Double,
    )

    private companion object {
        const val TAG = "AddressLocationRemote"
        const val SEARCH_RESULTS_LIMIT = 5
    }
}
