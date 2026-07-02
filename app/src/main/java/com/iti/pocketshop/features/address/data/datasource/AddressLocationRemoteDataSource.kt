package com.iti.pocketshop.features.address.data.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.data.model.GoogleAutocompleteResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeocodeResponse
import com.iti.pocketshop.features.address.data.model.GooglePlaceDetailsResponse

interface AddressLocationRemoteDataSource {
    suspend fun searchSuggestions(
        query: String,
        apiKey: String,
    ): PocketResult<GoogleAutocompleteResponse, PocketDataError.Remote>

    suspend fun resolveSuggestion(
        placeId: String,
        apiKey: String,
    ): PocketResult<GooglePlaceDetailsResponse, PocketDataError.Remote>

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String,
    ): PocketResult<GoogleGeocodeResponse, PocketDataError.Remote>
}
