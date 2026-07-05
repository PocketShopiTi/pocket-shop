package com.iti.pocketshop.features.address.presentation.action

import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.presentation.state.AddressField
import com.iti.pocketshop.features.address.utils.PhoneCountryCode

sealed interface AddressAction {
    data object Refresh : AddressAction
    data object Retry : AddressAction
    data object AddAddressClicked : AddressAction
    data class EditAddressClicked(val addressId: String) : AddressAction
    data object CloseEditor : AddressAction
    data class FieldChanged(val field: AddressField, val value: String) : AddressAction
    data class PhoneCountryChanged(val countryCode: PhoneCountryCode) : AddressAction
    data object ToggleDefault : AddressAction
    data object SaveClicked : AddressAction
    data class LocationPermissionResult(val granted: Boolean) : AddressAction
    data class LocationSearchChanged(val query: String) : AddressAction
    data class LocationSuggestionSelected(val suggestion: AddressLocationSuggestion) : AddressAction
    data class MapLocationPicked(val latitude: Double, val longitude: Double) : AddressAction
    data object ClearLocationSuggestions : AddressAction
    data class DeleteClicked(val addressId: String) : AddressAction
    data object ConfirmDelete : AddressAction
    data object CancelDelete : AddressAction
    data class SetDefaultClicked(val addressId: String) : AddressAction
    data object DismissError : AddressAction
    data object DismissMessage : AddressAction
}
