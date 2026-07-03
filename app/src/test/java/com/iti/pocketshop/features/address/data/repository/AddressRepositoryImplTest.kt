package com.iti.pocketshop.features.address.data.repository

import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.userdata.CustomerAccessTokenProvider
import com.iti.pocketshop.features.address.data.datasource.AddressLocationRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.AddressRemoteDataSource
import com.iti.pocketshop.features.address.data.datasource.CurrentLocationDataSource
import com.iti.pocketshop.features.address.data.model.GoogleAutocompleteResponse
import com.iti.pocketshop.features.address.data.model.GoogleGeocodeResponse
import com.iti.pocketshop.features.address.data.model.GooglePlaceDetailsResponse
import com.iti.pocketshop.features.address.domain.model.AddressDraft
import com.iti.pocketshop.features.address.domain.model.LocationCoordinates
import com.iti.pocketshop.shopify.CreateCustomerAddressMutation
import com.iti.pocketshop.shopify.DeleteCustomerAddressMutation
import com.iti.pocketshop.shopify.GetCustomerAddressesQuery
import com.iti.pocketshop.shopify.SetDefaultCustomerAddressMutation
import com.iti.pocketshop.shopify.UpdateCustomerAddressMutation
import com.iti.pocketshop.shopify.type.MailingAddressInput
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressRepositoryImplTest {

    @Test
    fun `address lifecycle stays in sync after each refresh`() = runBlocking {
        val remoteDataSource = FakeAddressRemoteDataSource(
            initialAddresses = listOf(
                StoredAddress(
                    id = "addr_1",
                    firstName = "Safa",
                    lastName = "Chen",
                    company = "",
                    phone = "+15550001",
                    address1 = "24 Pemberton Gardens",
                    address2 = "Flat 2",
                    city = "London",
                    province = "London",
                    zip = "N19 5RR",
                    country = "United Kingdom",
                    formattedArea = "London, England, United Kingdom",
                    latitude = 51.5678,
                    longitude = -0.1212,
                    isDefault = true,
                ),
            ),
        )
        val repository = AddressRepositoryImpl(
            remoteDataSource = remoteDataSource,
            addressLocationRemoteDataSource = FakeAddressLocationRemoteDataSource(),
            currentLocationDataSource = FakeCurrentLocationDataSource(),
            tokenProvider = FakeCustomerAccessTokenProvider("customer-token"),
            mapsApiKey = "maps-key",
            strings = TestAddressRepositoryStrings,
        )

        val initial = repository.getAddresses().successData()
        assertEquals(listOf("addr_1"), initial.addresses.map { it.id })
        assertEquals("addr_1", initial.defaultAddressId)
        assertTrue(initial.addresses.first().isDefault)

        val created = repository.saveAddress(
            addressId = null,
            draft = AddressDraft(
                firstName = "Amina",
                lastName = "Khaled",
                company = "Pocket",
                phone = "+15550002",
                address1 = "14 West Square",
                address2 = "Suite 8",
                city = "Manchester",
                province = "Greater Manchester",
                zip = "M15 6AB",
                country = "United Kingdom",
                isDefault = false,
            ),
        ).successData()
        assertEquals("addr_2", created.id)
        assertFalse(created.isDefault)

        val afterAdd = repository.getAddresses().successData()
        assertEquals(listOf("addr_1", "addr_2"), afterAdd.addresses.map { it.id })
        assertEquals("addr_1", afterAdd.defaultAddressId)

        val edited = repository.saveAddress(
            addressId = "addr_2",
            draft = AddressDraft(
                firstName = "Amina",
                lastName = "Khaled",
                company = "Pocket Labs",
                phone = "+15550003",
                address1 = "32 Baker Street",
                address2 = "Floor 3",
                city = "Manchester",
                province = "Greater Manchester",
                zip = "M15 6AB",
                country = "United Kingdom",
                isDefault = false,
            ),
        ).successData()
        assertEquals("Pocket Labs", edited.company)
        assertEquals("32 Baker Street", edited.address1)
        assertEquals("Manchester", edited.city)

        val afterEdit = repository.getAddresses().successData()
        val editedInList = afterEdit.addresses.first { it.id == "addr_2" }
        assertEquals("Pocket Labs", editedInList.company)
        assertEquals("32 Baker Street", editedInList.address1)
        assertEquals("+15550003", editedInList.phone)

        repository.setDefaultAddress("addr_2").successUnit()
        val afterDefault = repository.getAddresses().successData()
        assertEquals("addr_2", afterDefault.defaultAddressId)
        assertEquals(listOf("addr_2", "addr_1"), afterDefault.addresses.map { it.id })
        assertTrue(afterDefault.addresses.first().isDefault)

        repository.setDefaultAddress("addr_1").successUnit()
        val afterRevert = repository.getAddresses().successData()
        assertEquals("addr_1", afterRevert.defaultAddressId)
        assertEquals(listOf("addr_1", "addr_2"), afterRevert.addresses.map { it.id })
        assertTrue(afterRevert.addresses.first().isDefault)

        repository.deleteAddress("addr_2").successUnit()
        val afterDelete = repository.getAddresses().successData()
        assertEquals(listOf("addr_1"), afterDelete.addresses.map { it.id })
        assertEquals("addr_1", afterDelete.defaultAddressId)
    }

    private fun <D, E : com.iti.pocketshop.core.networkutils.Error> PocketResult<D, E>.successData(): D {
        return when (this) {
            is PocketResult.Success -> data
            is PocketResult.Error -> throw AssertionError("Expected success but got error: $error")
        }
    }

    private fun <E : com.iti.pocketshop.core.networkutils.Error> PocketResult<Unit, E>.successUnit() {
        when (this) {
            is PocketResult.Success -> Unit
            is PocketResult.Error -> throw AssertionError("Expected success but got error: $error")
        }
    }

    private data class StoredAddress(
        val id: String,
        var firstName: String,
        var lastName: String,
        var company: String,
        var phone: String,
        var address1: String,
        var address2: String,
        var city: String,
        var province: String,
        var zip: String,
        var country: String,
        var countryCode: String = "",
        var provinceCode: String = "",
        var formattedArea: String = "",
        var latitude: Double? = null,
        var longitude: Double? = null,
        var isDefault: Boolean = false,
    ) {
        fun toQueryNode(): GetCustomerAddressesQuery.Node {
            return GetCustomerAddressesQuery.Node(
                id = id,
                firstName = firstName.ifBlank { null },
                lastName = lastName.ifBlank { null },
                company = company.ifBlank { null },
                phone = phone.ifBlank { null },
                address1 = address1.ifBlank { null },
                address2 = address2.ifBlank { null },
                city = city.ifBlank { null },
                province = province.ifBlank { null },
                zip = zip.ifBlank { null },
                country = country.ifBlank { null },
                countryCodeV2 = null,
                provinceCode = provinceCode.ifBlank { null },
                formattedArea = formattedArea.ifBlank { null },
                latitude = latitude,
                longitude = longitude,
            )
        }

        fun toCreateCustomerAddress(): CreateCustomerAddressMutation.CustomerAddress {
            return CreateCustomerAddressMutation.CustomerAddress(
                id = id,
                firstName = firstName.ifBlank { null },
                lastName = lastName.ifBlank { null },
                company = company.ifBlank { null },
                phone = phone.ifBlank { null },
                address1 = address1.ifBlank { null },
                address2 = address2.ifBlank { null },
                city = city.ifBlank { null },
                province = province.ifBlank { null },
                zip = zip.ifBlank { null },
                country = country.ifBlank { null },
                countryCodeV2 = null,
                provinceCode = provinceCode.ifBlank { null },
                formattedArea = formattedArea.ifBlank { null },
                latitude = latitude,
                longitude = longitude,
            )
        }

        fun toUpdateCustomerAddress(): UpdateCustomerAddressMutation.CustomerAddress {
            return UpdateCustomerAddressMutation.CustomerAddress(
                id = id,
                firstName = firstName.ifBlank { null },
                lastName = lastName.ifBlank { null },
                company = company.ifBlank { null },
                phone = phone.ifBlank { null },
                address1 = address1.ifBlank { null },
                address2 = address2.ifBlank { null },
                city = city.ifBlank { null },
                province = province.ifBlank { null },
                zip = zip.ifBlank { null },
                country = country.ifBlank { null },
                countryCodeV2 = null,
                provinceCode = provinceCode.ifBlank { null },
                formattedArea = formattedArea.ifBlank { null },
                latitude = latitude,
                longitude = longitude,
            )
        }

        companion object {
            fun applyInput(id: String, input: MailingAddressInput, isDefault: Boolean): StoredAddress {
                val cityValue = input.city.stringOrEmpty()
                val provinceValue = input.province.stringOrEmpty()
                val zipValue = input.zip.stringOrEmpty()
                val countryValue = input.country.stringOrEmpty()

                return StoredAddress(
                    id = id,
                    firstName = input.firstName.stringOrEmpty(),
                    lastName = input.lastName.stringOrEmpty(),
                    company = input.company.stringOrEmpty(),
                    phone = input.phone.stringOrEmpty(),
                    address1 = input.address1.stringOrEmpty(),
                    address2 = input.address2.stringOrEmpty(),
                    city = cityValue,
                    province = provinceValue,
                    zip = zipValue,
                    country = countryValue,
                    formattedArea = listOf(cityValue, provinceValue, zipValue, countryValue)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    isDefault = isDefault,
                )
            }
        }
    }

    private class FakeAddressRemoteDataSource(
        initialAddresses: List<StoredAddress>,
    ) : AddressRemoteDataSource {
        private val addresses = initialAddresses.map { it.copy() }.toMutableList()
        private var nextId = (addresses.size + 1).coerceAtLeast(1)

        override suspend fun getAddresses(
            customerAccessToken: String,
        ): PocketResult<GetCustomerAddressesQuery.Data, PocketDataError.Remote> {
            return PocketResult.Success(
                GetCustomerAddressesQuery.Data(
                    customer = GetCustomerAddressesQuery.Customer(
                        id = "customer-1",
                        displayName = "Safa Chen",
                        defaultAddress = addresses.firstOrNull { it.isDefault }?.let {
                            GetCustomerAddressesQuery.DefaultAddress(it.id)
                        },
                        addresses = GetCustomerAddressesQuery.Addresses(
                            nodes = addresses.map { it.toQueryNode() },
                        ),
                    ),
                ),
            )
        }

        override suspend fun createAddress(
            customerAccessToken: String,
            address: MailingAddressInput,
        ): PocketResult<CreateCustomerAddressMutation.Data, PocketDataError.Remote> {
            val newAddress = StoredAddress.applyInput(
                id = "addr_$nextId",
                input = address,
                isDefault = false,
            )
            nextId += 1
            addresses += newAddress
            return PocketResult.Success(
                CreateCustomerAddressMutation.Data(
                    customerAddressCreate = CreateCustomerAddressMutation.CustomerAddressCreate(
                        customerAddress = newAddress.toCreateCustomerAddress(),
                        customerUserErrors = emptyList(),
                    ),
                ),
            )
        }

        override suspend fun updateAddress(
            customerAccessToken: String,
            addressId: String,
            address: MailingAddressInput,
        ): PocketResult<UpdateCustomerAddressMutation.Data, PocketDataError.Remote> {
            val index = addresses.indexOfFirst { it.id == addressId }
            check(index >= 0) { "Address not found: $addressId" }

            val existing = addresses[index]
            val updated = existing.copy(
                firstName = address.firstName.stringOrEmpty(),
                lastName = address.lastName.stringOrEmpty(),
                company = address.company.stringOrEmpty(),
                phone = address.phone.stringOrEmpty(),
                address1 = address.address1.stringOrEmpty(),
                address2 = address.address2.stringOrEmpty(),
                city = address.city.stringOrEmpty(),
                province = address.province.stringOrEmpty(),
                zip = address.zip.stringOrEmpty(),
                country = address.country.stringOrEmpty(),
                formattedArea = listOf(
                    address.city.stringOrEmpty(),
                    address.province.stringOrEmpty(),
                    address.zip.stringOrEmpty(),
                    address.country.stringOrEmpty(),
                ).filter { it.isNotBlank() }.joinToString(" · "),
            )
            addresses[index] = updated

            return PocketResult.Success(
                UpdateCustomerAddressMutation.Data(
                    customerAddressUpdate = UpdateCustomerAddressMutation.CustomerAddressUpdate(
                        customerAddress = updated.toUpdateCustomerAddress(),
                        customerUserErrors = emptyList(),
                    ),
                ),
            )
        }

        override suspend fun deleteAddress(
            customerAccessToken: String,
            addressId: String,
        ): PocketResult<DeleteCustomerAddressMutation.Data, PocketDataError.Remote> {
            val removed = addresses.removeAll { it.id == addressId }
            check(removed) { "Address not found: $addressId" }

            if (addresses.none { it.isDefault } && addresses.isNotEmpty()) {
                addresses.first().isDefault = true
            }

            return PocketResult.Success(
                DeleteCustomerAddressMutation.Data(
                    customerAddressDelete = DeleteCustomerAddressMutation.CustomerAddressDelete(
                        deletedCustomerAddressId = addressId,
                        customerUserErrors = emptyList(),
                    ),
                ),
            )
        }

        override suspend fun setDefaultAddress(
            customerAccessToken: String,
            addressId: String,
        ): PocketResult<SetDefaultCustomerAddressMutation.Data, PocketDataError.Remote> {
            val selected = addresses.firstOrNull { it.id == addressId }
            checkNotNull(selected) { "Address not found: $addressId" }

            addresses.forEach { it.isDefault = it.id == addressId }

            return PocketResult.Success(
                SetDefaultCustomerAddressMutation.Data(
                    customerDefaultAddressUpdate = SetDefaultCustomerAddressMutation.CustomerDefaultAddressUpdate(
                        customer = SetDefaultCustomerAddressMutation.Customer(
                            id = "customer-1",
                            defaultAddress = SetDefaultCustomerAddressMutation.DefaultAddress(
                                id = addressId,
                            ),
                        ),
                        customerUserErrors = emptyList(),
                    ),
                ),
            )
        }
    }

    private class FakeCustomerAccessTokenProvider(
        private val token: String,
    ) : CustomerAccessTokenProvider {
        override suspend fun currentCustomerAccessToken(): String? = token
    }

    private class FakeAddressLocationRemoteDataSource : AddressLocationRemoteDataSource {
        override suspend fun searchSuggestions(
            query: String,
            apiKey: String,
        ): PocketResult<GoogleAutocompleteResponse, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }

        override suspend fun resolveSuggestion(
            placeId: String,
            apiKey: String,
        ): PocketResult<GooglePlaceDetailsResponse, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }

        override suspend fun reverseGeocode(
            latitude: Double,
            longitude: Double,
            apiKey: String,
        ): PocketResult<GoogleGeocodeResponse, PocketDataError.Remote> {
            return PocketResult.Error(PocketDataError.Remote.UNKNOWN)
        }
    }

    private class FakeCurrentLocationDataSource : CurrentLocationDataSource {
        override suspend fun getCurrentLocation(): LocationCoordinates? = null
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

private fun Optional<String?>.stringOrEmpty(): String {
    return when (this) {
        is Optional.Present -> value.orEmpty()
        Optional.Absent -> ""
    }
}
