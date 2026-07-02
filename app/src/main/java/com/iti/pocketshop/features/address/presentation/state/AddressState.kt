package com.iti.pocketshop.features.address.presentation.state

import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.domain.model.AddressDraft
import com.iti.pocketshop.features.address.domain.model.AddressLocationDetails
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion

enum class AddressField {
    FIRST_NAME,
    LAST_NAME,
    COMPANY,
    PHONE,
    ADDRESS1,
    ADDRESS2,
    CITY,
    PROVINCE,
    ZIP,
    COUNTRY,
}

data class AddressState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val customerName: String = "",
    val addresses: List<Address> = emptyList(),
    val defaultAddressId: String? = null,
    val editor: AddressEditorState = AddressEditorState(),
    val pendingDeleteAddressId: String? = null,
    val error: AddressError? = null,
    val message: String? = null,
)

data class AddressEditorState(
    val visible: Boolean = false,
    val addressId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val company: String = "",
    val phone: String = "",
    val address1: String = "",
    val address2: String = "",
    val city: String = "",
    val province: String = "",
    val zip: String = "",
    val country: String = "",
    val formattedArea: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationSearchQuery: String = "",
    val locationSuggestions: List<AddressLocationSuggestion> = emptyList(),
    val isLocationSearching: Boolean = false,
    val isLocationResolving: Boolean = false,
    val isDefault: Boolean = false,
    val validationErrors: Map<AddressField, String> = emptyMap(),
) {
    val isEditing: Boolean
        get() = addressId != null

    fun toDraft(): AddressDraft {
        return AddressDraft(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            company = company.trim(),
            phone = phone.trim(),
            address1 = address1.trim(),
            address2 = address2.trim(),
            city = city.trim(),
            province = province.trim(),
            zip = zip.trim(),
            country = country.trim(),
            isDefault = isDefault,
        )
    }

    fun withLocationSuggestions(suggestions: List<AddressLocationSuggestion>): AddressEditorState {
        return copy(
            locationSuggestions = suggestions,
            isLocationSearching = false,
        )
    }

    fun withLocationSearch(query: String, isSearching: Boolean = false): AddressEditorState {
        return copy(
            locationSearchQuery = query,
            isLocationSearching = isSearching,
            locationSuggestions = emptyList(),
        )
    }

    fun withCoordinates(latitude: Double, longitude: Double): AddressEditorState {
        return copy(
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun withLocationSelection(details: AddressLocationDetails): AddressEditorState {
        val updatedErrors = validationErrors - setOf(
            AddressField.ADDRESS1,
            AddressField.CITY,
            AddressField.PROVINCE,
            AddressField.ZIP,
            AddressField.COUNTRY,
        )

        return copy(
            address1 = details.street.ifBlank { address1 },
            city = details.city.ifBlank { city },
            province = details.province.ifBlank { province },
            zip = details.postalCode.ifBlank { zip },
            country = details.country.ifBlank { country },
            formattedArea = details.formattedArea.ifBlank { formattedArea },
            latitude = details.latitude,
            longitude = details.longitude,
            locationSearchQuery = details.formattedAddress.ifBlank { locationSearchQuery },
            locationSuggestions = emptyList(),
            isLocationSearching = false,
            isLocationResolving = false,
            validationErrors = updatedErrors,
        )
    }

    fun withValidationErrors(errors: Map<AddressField, String>): AddressEditorState {
        return copy(validationErrors = errors)
    }

    fun clearFieldError(field: AddressField): AddressEditorState {
        return copy(validationErrors = validationErrors - field)
    }

    fun updateField(field: AddressField, value: String): AddressEditorState {
        val updated = when (field) {
            AddressField.FIRST_NAME -> copy(firstName = value)
            AddressField.LAST_NAME -> copy(lastName = value)
            AddressField.COMPANY -> copy(company = value)
            AddressField.PHONE -> copy(phone = value)
            AddressField.ADDRESS1 -> copy(address1 = value)
            AddressField.ADDRESS2 -> copy(address2 = value)
            AddressField.CITY -> copy(city = value)
            AddressField.PROVINCE -> copy(province = value)
            AddressField.ZIP -> copy(zip = value)
            AddressField.COUNTRY -> copy(country = value)
        }
        return updated.clearFieldError(field)
    }

    companion object {
        fun blank(isDefault: Boolean = false): AddressEditorState {
            return AddressEditorState(isDefault = isDefault)
        }

        fun fromAddress(address: Address): AddressEditorState {
            return AddressEditorState(
                visible = true,
                addressId = address.id,
                firstName = address.firstName,
                lastName = address.lastName,
                company = address.company,
                phone = address.phone,
                address1 = address.address1,
                address2 = address.address2,
                city = address.city,
                province = address.province,
                zip = address.zip,
                country = address.country,
                formattedArea = address.formattedArea,
                latitude = address.latitude,
                longitude = address.longitude,
                locationSearchQuery = address.formattedArea.ifBlank { address.locationLine },
                isDefault = address.isDefault,
            )
        }
    }
}
