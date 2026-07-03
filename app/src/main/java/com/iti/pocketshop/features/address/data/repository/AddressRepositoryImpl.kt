package com.iti.pocketshop.features.address.data.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.userdata.CustomerAccessTokenProvider
import com.iti.pocketshop.features.address.data.datasource.AddressRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.AddressLocationRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.CurrentLocationDataSource
import com.iti.pocketshop.features.address.data.model.GoogleAddressComponent
import com.iti.pocketshop.features.address.data.model.GoogleAddressResult
import com.iti.pocketshop.features.address.data.model.GoogleAutocompletePrediction
import com.iti.pocketshop.features.address.data.model.GoogleAutocompleteResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeocodeResponse
import com.iti.pocketshop.features.address.data.model.GooglePlaceDetailsResponse
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.domain.model.AddressBook
import com.iti.pocketshop.features.address.domain.model.AddressDraft
import com.iti.pocketshop.features.address.domain.model.AddressLocationDetails
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.domain.model.LocationCoordinates
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import com.iti.pocketshop.shopify.CreateCustomerAddressMutation
import com.iti.pocketshop.shopify.UpdateCustomerAddressMutation
import com.iti.pocketshop.shopify.type.MailingAddressInput
import javax.inject.Named
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val remoteDataSource: AddressRemoteDataSource,
    private val addressLocationRemoteDataSource: AddressLocationRemoteDataSource,
    private val currentLocationDataSource: CurrentLocationDataSource,
    private val tokenProvider: CustomerAccessTokenProvider,
    @Named("mapsApiKey") private val mapsApiKey: String,
    private val strings: AddressRepositoryStrings,
) : AddressRepository {

    override suspend fun getAddresses(): PocketResult<AddressBook, AddressError> {
        val customerAccessToken = tokenOrError() ?: return PocketResult.Error(
            AddressError.MissingCustomerAccessToken,
        )

        return when (val result = remoteDataSource.getAddresses(customerAccessToken)) {
            is PocketResult.Error -> PocketResult.Error(result.error.toAddressError())
            is PocketResult.Success -> {
                val customer = result.data.customer ?: return PocketResult.Error(
                    AddressError.Shopify(listOf(strings.customerAccountNotFound)),
                )
                val defaultAddressId = customer.defaultAddress?.id
                val addresses = customer.addresses.nodes
                    .map { node ->
                        Address(
                            id = node.id,
                            firstName = node.firstName.orEmpty(),
                            lastName = node.lastName.orEmpty(),
                            company = node.company.orEmpty(),
                            phone = node.phone.orEmpty(),
                            address1 = node.address1.orEmpty(),
                            address2 = node.address2.orEmpty(),
                            city = node.city.orEmpty(),
                            province = node.province.orEmpty(),
                            zip = node.zip.orEmpty(),
                            country = node.country.orEmpty(),
                            countryCode = node.countryCodeV2?.rawValue.orEmpty(),
                            provinceCode = node.provinceCode.orEmpty(),
                            formattedArea = node.formattedArea.orEmpty(),
                            latitude = node.latitude,
                            longitude = node.longitude,
                            isDefault = node.id == defaultAddressId,
                        )
                    }
                    .sortedByDescending { it.isDefault }

                PocketResult.Success(
                    AddressBook(
                        customerName = customer.displayName,
                        addresses = addresses,
                        defaultAddressId = defaultAddressId,
                    ),
                )
            }
        }
    }

    override suspend fun saveAddress(
        addressId: String?,
        draft: AddressDraft,
    ): PocketResult<Address, AddressError> {
        val customerAccessToken = tokenOrError() ?: return PocketResult.Error(
            AddressError.MissingCustomerAccessToken,
        )

        val input = draft.toMailingAddressInput()

        val savedAddress = if (addressId == null) {
            when (val result = remoteDataSource.createAddress(customerAccessToken, input)) {
                is PocketResult.Error -> return PocketResult.Error(result.error.toAddressError())
                is PocketResult.Success -> {
                    val payload = result.data.customerAddressCreate ?: return PocketResult.Error(
                        AddressError.Shopify(listOf(strings.missingSavedAddress)),
                    )
                    val userErrors = payload.customerUserErrors
                        .map { it.message }
                        .filter { it.isNotBlank() }
                    if (userErrors.isNotEmpty()) {
                        return PocketResult.Error(AddressError.Shopify(userErrors))
                    }
                    val customerAddress = payload.customerAddress ?: return PocketResult.Error(
                        AddressError.Shopify(listOf(strings.missingSavedAddress)),
                    )
                    customerAddress.toDomain(isDefault = draft.isDefault)
                }
            }
        } else {
            when (val result = remoteDataSource.updateAddress(customerAccessToken, addressId, input)) {
                is PocketResult.Error -> return PocketResult.Error(result.error.toAddressError())
                is PocketResult.Success -> {
                    val payload = result.data.customerAddressUpdate ?: return PocketResult.Error(
                        AddressError.Shopify(listOf(strings.missingUpdatedAddress)),
                    )
                    val userErrors = payload.customerUserErrors
                        .map { it.message }
                        .filter { it.isNotBlank() }
                    if (userErrors.isNotEmpty()) {
                        return PocketResult.Error(AddressError.Shopify(userErrors))
                    }
                    val customerAddress = payload.customerAddress ?: return PocketResult.Error(
                        AddressError.Shopify(listOf(strings.missingUpdatedAddress)),
                    )
                    customerAddress.toDomain(isDefault = draft.isDefault)
                }
            }
        }

        if (draft.isDefault) {
            when (val defaultResult = remoteDataSource.setDefaultAddress(customerAccessToken, savedAddress.id)) {
                is PocketResult.Error -> return PocketResult.Error(defaultResult.error.toAddressError())
                is PocketResult.Success -> Unit
            }
        }

        return PocketResult.Success(savedAddress)
    }

    override suspend fun deleteAddress(addressId: String): PocketResult<Unit, AddressError> {
        val customerAccessToken = tokenOrError() ?: return PocketResult.Error(
            AddressError.MissingCustomerAccessToken,
        )

        return when (val result = remoteDataSource.deleteAddress(customerAccessToken, addressId)) {
            is PocketResult.Error -> PocketResult.Error(result.error.toAddressError())
            is PocketResult.Success -> {
                val payload = result.data.customerAddressDelete ?: return PocketResult.Error(
                    AddressError.Shopify(listOf(strings.missingDeletedAddress)),
                )
                val userErrors = payload.customerUserErrors
                    .map { it.message }
                    .filter { it.isNotBlank() }
                if (userErrors.isNotEmpty()) {
                    PocketResult.Error(AddressError.Shopify(userErrors))
                } else {
                    PocketResult.Success(Unit)
                }
            }
        }
    }

    override suspend fun setDefaultAddress(addressId: String): PocketResult<Unit, AddressError> {
        val customerAccessToken = tokenOrError() ?: return PocketResult.Error(
            AddressError.MissingCustomerAccessToken,
        )

        return when (val result = remoteDataSource.setDefaultAddress(customerAccessToken, addressId)) {
            is PocketResult.Error -> PocketResult.Error(result.error.toAddressError())
            is PocketResult.Success -> {
                val payload = result.data.customerDefaultAddressUpdate ?: return PocketResult.Error(
                    AddressError.Shopify(listOf(strings.missingDefaultUpdate)),
                )
                val userErrors = payload.customerUserErrors
                    .map { it.message }
                    .filter { it.isNotBlank() }
                if (userErrors.isNotEmpty()) {
                    PocketResult.Error(AddressError.Shopify(userErrors))
                } else {
                    PocketResult.Success(Unit)
                }
            }
        }
    }

    override suspend fun searchSuggestions(
        query: String,
    ): PocketResult<List<AddressLocationSuggestion>, AddressError> {
        if (query.isBlank()) {
            return PocketResult.Success(emptyList())
        }

        return when (val result = addressLocationRemoteDataSource.searchSuggestions(query, mapsApiKey)) {
            is PocketResult.Error -> PocketResult.Error(result.error.toAddressError())
            is PocketResult.Success -> result.data.toDomainSuggestions()
        }
    }

    override suspend fun resolveSuggestion(
        placeId: String,
    ): PocketResult<AddressLocationDetails, AddressError> {
        return when (val result = addressLocationRemoteDataSource.resolveSuggestion(placeId, mapsApiKey)) {
            is PocketResult.Error -> PocketResult.Error(result.error.toAddressError())
            is PocketResult.Success -> result.data.toDomainDetails()
        }
    }

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): PocketResult<AddressLocationDetails, AddressError> {
        return when (val result = addressLocationRemoteDataSource.reverseGeocode(latitude, longitude, mapsApiKey)) {
            is PocketResult.Error -> PocketResult.Error(result.error.toAddressError())
            is PocketResult.Success -> result.data.toDomainGeocodeDetails()
        }
    }

    override suspend fun getCurrentLocation(): PocketResult<LocationCoordinates, AddressError> {
        val coordinates = currentLocationDataSource.getCurrentLocation()
            ?: return PocketResult.Error(AddressError.CurrentLocationUnavailable)

        return PocketResult.Success(coordinates)
    }

    private suspend fun tokenOrError(): String? {
        return tokenProvider.currentCustomerAccessToken()?.takeIf { it.isNotBlank() }
    }

    private fun PocketDataError.Remote.toAddressError(): AddressError = AddressError.Remote(this)

    private fun GoogleAutocompletePrediction.toDomain(): AddressLocationSuggestion? {
        if (placeId.isBlank()) {
            return null
        }

        val structured = structuredFormatting
        return AddressLocationSuggestion(
            placeId = placeId,
            primaryText = structured?.mainText?.ifBlank { description }.orEmpty().ifBlank { description },
            secondaryText = structured?.secondaryText.orEmpty(),
        )
    }

    private fun GoogleAutocompleteResponse.toDomainSuggestions(): PocketResult<List<AddressLocationSuggestion>, AddressError> {
        return when (status) {
            "OK", "ZERO_RESULTS" -> {
                val predictions = predictions
                    .mapNotNull { prediction -> prediction.toDomain() }
                PocketResult.Success(predictions)
            }

            "OVER_QUERY_LIMIT" ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.TOO_MANY_REQUESTS),
                )

            "REQUEST_DENIED", "INVALID_REQUEST" ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.SERVER),
                )

            else ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.UNKNOWN),
                )
        }
    }

    private fun GooglePlaceDetailsResponse.toDomainDetails(): PocketResult<AddressLocationDetails, AddressError> {
        return when (status) {
            "OK" -> {
                val place = result ?: return PocketResult.Error(AddressError.LocationNotFound)
                val details = place.toDomain() ?: return PocketResult.Error(AddressError.LocationNotFound)
                PocketResult.Success(details)
            }

            "ZERO_RESULTS" ->
                PocketResult.Error(AddressError.LocationNotFound)

            "OVER_QUERY_LIMIT" ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.TOO_MANY_REQUESTS),
                )

            "REQUEST_DENIED", "INVALID_REQUEST" ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.SERVER),
                )

            else ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.UNKNOWN),
                )
        }
    }

    private fun GoogleGeocodeResponse.toDomainGeocodeDetails(): PocketResult<AddressLocationDetails, AddressError> {
        return when (status) {
            "OK" -> {
                val location = results.firstOrNull() ?: return PocketResult.Error(AddressError.LocationNotFound)
                val details = location.toDomain() ?: return PocketResult.Error(AddressError.LocationNotFound)
                PocketResult.Success(details)
            }

            "ZERO_RESULTS" ->
                PocketResult.Error(AddressError.LocationNotFound)

            "OVER_QUERY_LIMIT" ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.TOO_MANY_REQUESTS),
                )

            "REQUEST_DENIED", "INVALID_REQUEST" ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.SERVER),
                )

            else ->
                PocketResult.Error(
                    AddressError.Remote(PocketDataError.Remote.UNKNOWN),
                )
        }
    }

    private fun GoogleAddressResult.toDomain(): AddressLocationDetails? {
        val location = geometry?.location ?: return null
        val latitude = location.lat ?: return null
        val longitude = location.lng ?: return null
        val componentMap = addressComponents.associateBy { it.primaryType() }

        val streetNumber = componentMap["street_number"]?.longName.orEmpty()
        val route = componentMap["route"]?.longName.orEmpty()
        val street = listOf(streetNumber, route)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { formattedAddress.orEmpty() }

        val city = componentMap["locality"]?.longName
            .orEmpty()
            .ifBlank { componentMap["postal_town"]?.longName.orEmpty() }
            .ifBlank { componentMap["administrative_area_level_2"]?.longName.orEmpty() }

        val province = componentMap["administrative_area_level_1"]?.longName.orEmpty()
        val country = componentMap["country"]?.longName.orEmpty()
        val countryCode = componentMap["country"]?.shortName.orEmpty()
        val postalCode = componentMap["postal_code"]?.longName.orEmpty()

        return AddressLocationDetails(
            formattedAddress = formattedAddress.orEmpty(),
            street = street,
            city = city,
            province = province,
            country = country,
            postalCode = postalCode,
            latitude = latitude,
            longitude = longitude,
            countryCode = countryCode,
        )
    }

    private fun GoogleAddressComponent.primaryType(): String? {
        return types.firstOrNull()
    }

    private fun CreateCustomerAddressMutation.CustomerAddress.toDomain(
        isDefault: Boolean,
    ): Address {
        return Address(
            id = id,
            firstName = firstName.orEmpty(),
            lastName = lastName.orEmpty(),
            company = company.orEmpty(),
            phone = phone.orEmpty(),
            address1 = address1.orEmpty(),
            address2 = address2.orEmpty(),
            city = city.orEmpty(),
            province = province.orEmpty(),
            zip = zip.orEmpty(),
            country = country.orEmpty(),
            countryCode = countryCodeV2?.rawValue.orEmpty(),
            provinceCode = provinceCode.orEmpty(),
            formattedArea = formattedArea.orEmpty(),
            latitude = latitude,
            longitude = longitude,
            isDefault = isDefault,
        )
    }

    private fun UpdateCustomerAddressMutation.CustomerAddress.toDomain(
        isDefault: Boolean,
    ): Address {
        return Address(
            id = id,
            firstName = firstName.orEmpty(),
            lastName = lastName.orEmpty(),
            company = company.orEmpty(),
            phone = phone.orEmpty(),
            address1 = address1.orEmpty(),
            address2 = address2.orEmpty(),
            city = city.orEmpty(),
            province = province.orEmpty(),
            zip = zip.orEmpty(),
            country = country.orEmpty(),
            countryCode = countryCodeV2?.rawValue.orEmpty(),
            provinceCode = provinceCode.orEmpty(),
            formattedArea = formattedArea.orEmpty(),
            latitude = latitude,
            longitude = longitude,
            isDefault = isDefault,
        )
    }

    private fun AddressDraft.toMailingAddressInput(): MailingAddressInput {
        return MailingAddressInput(
            address1 = present(address1.trim()),
            address2 = optional(address2),
            city = optional(city),
            company = optional(company),
            country = present(country.trim()),
            firstName = present(firstName.trim()),
            lastName = present(lastName.trim()),
            phone = optional(phone),
            province = Optional.Absent,
            zip = present(zip.trim()),
        )
    }

    private fun present(value: String): Optional<String?> {
        return Optional.Present(value)
    }

    private fun optional(value: String): Optional<String?> {
        val trimmed = value.trim()
        return if (trimmed.isBlank()) {
            Optional.Absent
        } else {
            Optional.Present(trimmed)
        }
    }
}
