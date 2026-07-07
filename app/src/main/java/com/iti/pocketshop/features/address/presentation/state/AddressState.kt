package com.iti.pocketshop.features.address.presentation.state

import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.domain.model.AddressDraft
import com.iti.pocketshop.features.address.domain.model.AddressLocationDetails
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.utils.PhoneCountryCode
import androidx.annotation.StringRes

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
    @StringRes val messageId: Int? = null,
)

data class AddressEditorState(
    val visible: Boolean = false,
    val addressId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val company: String = "",
    val phone: String = "",
    val phoneCountryCode: PhoneCountryCode = PhoneCountryCode.default(),
    val phoneCountryCodeTouched: Boolean = false,
    val address1: String = "",
    val address2: String = "",
    val city: String = "",
    val province: String = "",
    val provinceCode: String = "",
    val zip: String = "",
    val country: String = "",
    val formattedArea: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationSearchQuery: String = "",
    val locationSuggestions: List<AddressLocationSuggestion> = emptyList(),
    val isLocationSearching: Boolean = false,
    val isLocationResolving: Boolean = false,
    val hasSearchResult: Boolean = false,
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
            phone = phoneCountryCode.toE164(phone.trim()) ?: phone.trim(),
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
            hasSearchResult = true,
        )
    }

    fun withLocationSearch(query: String, isSearching: Boolean = false): AddressEditorState {
        return copy(
            locationSearchQuery = query,
            isLocationSearching = isSearching,
            locationSuggestions = emptyList(),
            hasSearchResult = false,
        )
    }

    fun withCoordinates(latitude: Double, longitude: Double): AddressEditorState {
        return copy(
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun withLocationSelection(
        details: AddressLocationDetails,
        updateSearchQuery: Boolean = false,
    ): AddressEditorState {
        val updatedErrors = validationErrors - setOf(
            AddressField.ADDRESS1,
            AddressField.CITY,
            AddressField.PROVINCE,
            AddressField.COUNTRY,
            AddressField.PHONE,
        )

        return copy(
            address1 = details.street.ifBlank { address1 },
            city = details.city.ifBlank { city },
            province = details.province.ifBlank { province },
            country = details.country.ifBlank { country },
            phoneCountryCode = if (phoneCountryCodeTouched) {
                phoneCountryCode
            } else {
                PhoneCountryCode.fromCountryCode(details.countryCode)
                    ?: PhoneCountryCode.fromCountry(details.country)
            },
            formattedArea = details.formattedArea.ifBlank { formattedArea },
            latitude = details.latitude,
            longitude = details.longitude,
            locationSearchQuery = if (updateSearchQuery) {
                details.formattedAddress.ifBlank { locationSearchQuery }
            } else {
                locationSearchQuery
            },
            locationSuggestions = emptyList(),
            isLocationSearching = false,
            isLocationResolving = false,
            hasSearchResult = false,
            validationErrors = updatedErrors,
        )
    }

    fun withValidationErrors(errors: Map<AddressField, String>): AddressEditorState {
        return copy(validationErrors = errors)
    }

    fun withPhoneCountryCode(countryCode: PhoneCountryCode): AddressEditorState {
        return copy(
            phoneCountryCode = countryCode,
            phoneCountryCodeTouched = true,
            validationErrors = validationErrors - AddressField.PHONE,
        )
    }

    fun withContactPicked(displayName: String?, phoneNumber: String?): AddressEditorState {
        val resolvedCountryCode = when {
            phoneNumber.isNullOrBlank() -> phoneCountryCode
            phoneCountryCodeTouched -> phoneCountryCode
            else -> PhoneCountryCode.fromSavedPhone(phone = phoneNumber)
        }
        val resolvedPhone = phoneNumber?.takeIf { it.isNotBlank() }
            ?.let { resolvedCountryCode.displayNumber(it) }
            ?: phone

        val nameParts = displayName
            ?.trim()
            ?.split(Regex("\\s+"))
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        val resolvedFirstName = firstName.ifBlank { nameParts.firstOrNull().orEmpty() }
        val resolvedLastName = lastName.ifBlank {
            if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""
        }

        return copy(
            phone = resolvedPhone,
            phoneCountryCode = resolvedCountryCode,
            firstName = resolvedFirstName,
            lastName = resolvedLastName,
            validationErrors = validationErrors - setOf(
                AddressField.PHONE,
                AddressField.FIRST_NAME,
                AddressField.LAST_NAME,
            ),
        )
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
            AddressField.PROVINCE -> copy(province = value, provinceCode = value)
            AddressField.ZIP -> copy(zip = value)
            AddressField.COUNTRY -> copy(
                country = value,
                phoneCountryCode = if (phoneCountryCodeTouched) {
                    phoneCountryCode
                } else {
                    PhoneCountryCode.fromCountry(value)
                },
            ).clearFieldError(AddressField.PHONE)
        }
        return updated.clearFieldError(field)
    }

    companion object {
        fun blank(isDefault: Boolean = false): AddressEditorState {
            return AddressEditorState(isDefault = isDefault)
        }

        fun fromAddress(address: Address): AddressEditorState {
            val phoneCountryCode = PhoneCountryCode.fromSavedPhone(
                phone = address.phone,
                countryCode = address.countryCode,
                country = address.country,
            )

            return AddressEditorState(
                visible = true,
                addressId = address.id,
                firstName = address.firstName,
                lastName = address.lastName,
                company = address.company,
                phone = phoneCountryCode.displayNumber(address.phone),
                phoneCountryCode = phoneCountryCode,
                address1 = address.address1,
                address2 = address.address2,
                city = address.city,
                province = address.province,
                provinceCode = address.provinceCode,
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
