package com.iti.pocketshop.features.address.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleAutocompleteResponse(
    val predictions: List<GoogleAutocompletePrediction> = emptyList(),
    val status: String = "",
    @SerialName("error_message")
    val errorMessage: String? = null,
)

@Serializable
data class GoogleAutocompletePrediction(
    @SerialName("place_id")
    val placeId: String = "",
    val description: String = "",
    @SerialName("structured_formatting")
    val structuredFormatting: GoogleStructuredFormatting? = null,
)

@Serializable
data class GoogleStructuredFormatting(
    @SerialName("main_text")
    val mainText: String = "",
    @SerialName("secondary_text")
    val secondaryText: String = "",
)

@Serializable
data class GooglePlaceDetailsResponse(
    val result: GoogleAddressResult? = null,
    val status: String = "",
    @SerialName("error_message")
    val errorMessage: String? = null,
)

@Serializable
data class GoogleGeocodeResponse(
    val results: List<GoogleAddressResult> = emptyList(),
    val status: String = "",
    @SerialName("error_message")
    val errorMessage: String? = null,
)

@Serializable
data class GoogleAddressResult(
    @SerialName("formatted_address")
    val formattedAddress: String? = null,
    @SerialName("address_components")
    val addressComponents: List<GoogleAddressComponent> = emptyList(),
    val geometry: GoogleGeometry? = null,
)

@Serializable
data class GoogleAddressComponent(
    @SerialName("long_name")
    val longName: String = "",
    @SerialName("short_name")
    val shortName: String = "",
    val types: List<String> = emptyList(),
)

@Serializable
data class GoogleGeometry(
    val location: GoogleLatLng? = null,
)

@Serializable
data class GoogleLatLng(
    val lat: Double? = null,
    val lng: Double? = null,
)
