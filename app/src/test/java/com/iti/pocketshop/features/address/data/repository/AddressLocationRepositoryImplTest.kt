package com.iti.pocketshop.features.address.data.repository

import com.iti.pocketshop.com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.core.userdata.CustomerAccessTokenProvider
import com.iti.pocketshop.features.address.data.datasource.AddressLocationRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.AddressRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.CurrentLocationDataSource
import com.iti.pocketshop.features.address.data.model.GoogleAddressComponent
import com.iti.pocketshop.features.address.data.model.GoogleAddressResult
import com.iti.pocketshop.features.address.data.model.GoogleAutocompletePrediction
import com.iti.pocketshop.features.address.data.model.GoogleAutocompleteResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeocodeResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeometry
import com.iti.pocketshop.features.address.data.model.GoogleLatLng
import com.iti.pocketshop.features.address.data.model.GooglePlaceDetailsResponse
import com.iti.pocketshop.features.address.data.model.GoogleStructuredFormatting
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.LocationCoordinates
import com.iti.pocketshop.shopify.CreateCustomerAddressMutation
import com.iti.pocketshop.shopify.DeleteCustomerAddressMutation
import com.iti.pocketshop.shopify.GetCustomerAddressesQuery
import com.iti.pocketshop.shopify.SetDefaultCustomerAddressMutation
import com.iti.pocketshop.shopify.UpdateCustomerAddressMutation
import com.iti.pocketshop.shopify.type.MailingAddressInput
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressRepositoryLocationMethodsTest {

    @Test
    fun `searchSuggestions should map google predictions to domain suggestions`() = runBlocking {
        val repository = repository(
            locationRemoteDataSource = FakeAddressLocationRemoteDataSource(
                searchResult = PocketResult.Success(
                    GoogleAutocompleteResponse(
                        status = "OK",
                        predictions = listOf(
                            GoogleAutocompletePrediction(
                                placeId = "place-1",
                                description = "24 Pemberton Gardens, London, UK",
                                structuredFormatting = GoogleStructuredFormatting(
                                    mainText = "24 Pemberton Gardens",
                                    secondaryText = "London, UK",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = repository.searchSuggestions("pemberton")

        assertTrue(result is PocketResult.Success)
        val suggestions = (result as PocketResult.Success).data
        assertEquals(1, suggestions.size)
        assertEquals("place-1", suggestions.first().placeId)
        assertEquals("24 Pemberton Gardens", suggestions.first().primaryText)
        assertEquals("London, UK", suggestions.first().secondaryText)
    }

    @Test
    fun `reverseGeocode should map components into a domain address`() = runBlocking {
        val repository = repository(
            locationRemoteDataSource = FakeAddressLocationRemoteDataSource(
                geocodeResult = PocketResult.Success(
                    GoogleGeocodeResponse(
                        status = "OK",
                        results = listOf(
                            GoogleAddressResult(
                                formattedAddress = "24 Pemberton Gardens, London N19 5RR, UK",
                                addressComponents = listOf(
                                    GoogleAddressComponent(
                                        longName = "24",
                                        shortName = "24",
                                        types = listOf("street_number"),
                                    ),
                                    GoogleAddressComponent(
                                        longName = "Pemberton Gardens",
                                        shortName = "Pemberton Gardens",
                                        types = listOf("route"),
                                    ),
                                    GoogleAddressComponent(
                                        longName = "London",
                                        shortName = "London",
                                        types = listOf("locality"),
                                    ),
                                    GoogleAddressComponent(
                                        longName = "London",
                                        shortName = "ENG",
                                        types = listOf("administrative_area_level_1"),
                                    ),
                                    GoogleAddressComponent(
                                        longName = "United Kingdom",
                                        shortName = "GB",
                                        types = listOf("country"),
                                    ),
                                    GoogleAddressComponent(
                                        longName = "N19 5RR",
                                        shortName = "N19 5RR",
                                        types = listOf("postal_code"),
                                    ),
                                ),
                                geometry = GoogleGeometry(
                                    location = GoogleLatLng(
                                        lat = 51.5678,
                                        lng = -0.1212,
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = repository.reverseGeocode(51.5678, -0.1212)

        assertTrue(result is PocketResult.Success)
        val details = (result as PocketResult.Success).data
        assertEquals("24 Pemberton Gardens, London N19 5RR, UK", details.formattedAddress)
        assertEquals("24 Pemberton Gardens", details.street)
        assertEquals("London", details.city)
        assertEquals("London", details.province)
        assertEquals("United Kingdom", details.country)
        assertEquals("N19 5RR", details.postalCode)
        assertEquals(51.5678, details.latitude, 0.0)
        assertEquals(-0.1212, details.longitude, 0.0)
    }

    @Test
    fun `resolveSuggestion should surface location not found when google returns zero results`() = runBlocking {
        val repository = repository(
            locationRemoteDataSource = FakeAddressLocationRemoteDataSource(
                detailsResult = PocketResult.Success(
                    GooglePlaceDetailsResponse(
                        status = "ZERO_RESULTS",
                        result = null,
                    ),
                ),
            ),
        )

        val result = repository.resolveSuggestion("missing-place")

        assertTrue(result is PocketResult.Error)
        assertEquals(AddressError.LocationNotFound, (result as PocketResult.Error).error)
    }

    @Test
    fun `getCurrentLocation should return coordinates from the location data source`() = runBlocking {
        val repository = repository(
            currentLocationDataSource = FakeCurrentLocationDataSource(
                LocationCoordinates(
                    latitude = 51.5678,
                    longitude = -0.1212,
                ),
            ),
        )

        val result = repository.getCurrentLocation()

        assertTrue(result is PocketResult.Success)
        val coordinates = (result as PocketResult.Success).data
        assertEquals(51.5678, coordinates.latitude, 0.0)
        assertEquals(-0.1212, coordinates.longitude, 0.0)
    }

    private fun repository(
        locationRemoteDataSource: AddressLocationRemoteDataSource = FakeAddressLocationRemoteDataSource(),
        currentLocationDataSource: CurrentLocationDataSource = FakeCurrentLocationDataSource(),
    ): AddressRepositoryImpl {
        return AddressRepositoryImpl(
            remoteDataSource = NoopAddressRemoteDataSource(),
            addressLocationRemoteDataSource = locationRemoteDataSource,
            currentLocationDataSource = currentLocationDataSource,
            tokenProvider = FakeCustomerAccessTokenProvider("customer-token"),
            mapsApiKey = "maps-key",
            strings = TestAddressRepositoryStrings,
        )
    }

    private class NoopAddressRemoteDataSource : AddressRemoteDataSource {
        override suspend fun getAddresses(
            customerAccessToken: String,
        ): PocketResult<GetCustomerAddressesQuery.Data, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }

        override suspend fun createAddress(
            customerAccessToken: String,
            address: MailingAddressInput,
        ): PocketResult<CreateCustomerAddressMutation.Data, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }

        override suspend fun updateAddress(
            customerAccessToken: String,
            addressId: String,
            address: MailingAddressInput,
        ): PocketResult<UpdateCustomerAddressMutation.Data, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }

        override suspend fun deleteAddress(
            customerAccessToken: String,
            addressId: String,
        ): PocketResult<DeleteCustomerAddressMutation.Data, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }

        override suspend fun setDefaultAddress(
            customerAccessToken: String,
            addressId: String,
        ): PocketResult<SetDefaultCustomerAddressMutation.Data, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }
    }

    private class FakeAddressLocationRemoteDataSource(
        private val searchResult: PocketResult<GoogleAutocompleteResponse, PocketDataError.Remote> = PocketResult.Success(
            GoogleAutocompleteResponse(status = "ZERO_RESULTS"),
        ),
        private val detailsResult: PocketResult<GooglePlaceDetailsResponse, PocketDataError.Remote> = PocketResult.Success(
            GooglePlaceDetailsResponse(status = "ZERO_RESULTS"),
        ),
        private val geocodeResult: PocketResult<GoogleGeocodeResponse, PocketDataError.Remote> = PocketResult.Success(
            GoogleGeocodeResponse(status = "ZERO_RESULTS"),
        ),
    ) : AddressLocationRemoteDataSource {
        override suspend fun searchSuggestions(
            query: String,
            apiKey: String,
        ): PocketResult<GoogleAutocompleteResponse, PocketDataError.Remote> = searchResult

        override suspend fun resolveSuggestion(
            placeId: String,
            apiKey: String,
        ): PocketResult<GooglePlaceDetailsResponse, PocketDataError.Remote> = detailsResult

        override suspend fun reverseGeocode(
            latitude: Double,
            longitude: Double,
            apiKey: String,
        ): PocketResult<GoogleGeocodeResponse, PocketDataError.Remote> = geocodeResult
    }

    private class FakeCurrentLocationDataSource(
        private val coordinates: LocationCoordinates? = null,
    ) : CurrentLocationDataSource {
        override suspend fun getCurrentLocation(): LocationCoordinates? = coordinates
    }

    private class FakeCustomerAccessTokenProvider(
        private val token: String,
    ) : CustomerAccessTokenProvider {
        override suspend fun currentCustomerAccessToken(): String? = token
    }

    private object TestAddressRepositoryStrings : AddressRepositoryStrings {
        override val missingCustomerAccessToken: String = "Customer account token is not available yet."
        override val customerAccountNotFound: String = "Customer account was not found for this token."
        override val missingSavedAddress: String = "Shopify did not return the saved address."
        override val missingUpdatedAddress: String = "Shopify did not return the updated address."
        override val missingDeletedAddress: String = "Shopify did not confirm the deleted address."
        override val missingDefaultUpdate: String = "Shopify did not confirm the default address update."
    }
}
