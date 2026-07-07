package com.iti.pocketshop.features.address.domain.repository

import android.net.Uri
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.domain.model.AddressBook
import com.iti.pocketshop.features.address.domain.model.AddressDraft
import com.iti.pocketshop.features.address.domain.model.AddressLocationDetails
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.domain.model.ContactInfo
import com.iti.pocketshop.features.address.domain.model.LocationCoordinates

interface AddressRepository {
    suspend fun getAddresses(): PocketResult<AddressBook, AddressError>

    suspend fun saveAddress(
        addressId: String? = null,
        draft: AddressDraft,
    ): PocketResult<Address, AddressError>

    suspend fun deleteAddress(addressId: String): PocketResult<Unit, AddressError>

    suspend fun setDefaultAddress(addressId: String): PocketResult<Unit, AddressError>

    suspend fun searchSuggestions(
        query: String,
    ): PocketResult<List<AddressLocationSuggestion>, AddressError>

    suspend fun resolveSuggestion(
        placeId: String,
    ): PocketResult<AddressLocationDetails, AddressError>

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): PocketResult<AddressLocationDetails, AddressError>

    suspend fun getCurrentLocation(): PocketResult<LocationCoordinates, AddressError>
    suspend fun getContact(uri: Uri): ContactInfo?

}
