package com.iti.pocketshop.features.address.presentation.viewmodel

import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressState

internal fun reduceAddressState(
    state: AddressState,
    action: AddressAction,
): AddressState = when (action) {
    AddressAction.AddAddressClicked -> state.copy(
        editor = AddressEditorState.blank(isDefault = state.addresses.isEmpty()).copy(visible = true),
        pendingDeleteAddressId = null,
        error = null,
        message = null,
    )

    is AddressAction.EditAddressClicked -> {
        val address = state.addresses.firstOrNull { it.id == action.addressId } ?: return state
        state.copy(
            editor = AddressEditorState.fromAddress(address),
            pendingDeleteAddressId = null,
            error = null,
            message = null,
        )
    }

    AddressAction.CloseEditor -> state.copy(
        editor = AddressEditorState(),
        pendingDeleteAddressId = null,
        error = null,
        message = null,
    )

    is AddressAction.FieldChanged -> state.copy(
        editor = state.editor.updateField(action.field, action.value),
        error = null,
    )

    is AddressAction.PhoneCountryChanged -> state.copy(
        editor = state.editor.withPhoneCountryCode(action.countryCode),
        error = null,
    )

    is AddressAction.LocationSearchChanged -> state.copy(
        editor = state.editor.withLocationSearch(action.query),
        error = null,
    )

    AddressAction.ClearLocationSuggestions -> state.copy(
        editor = state.editor.withLocationSuggestions(emptyList()),
    )

    AddressAction.ToggleDefault -> state.copy(
        editor = state.editor.copy(isDefault = !state.editor.isDefault),
    )

    is AddressAction.DeleteClicked -> state.copy(
        pendingDeleteAddressId = action.addressId,
        error = null,
        message = null,
    )

    AddressAction.CancelDelete -> state.copy(
        pendingDeleteAddressId = null,
    )

    AddressAction.DismissError -> state.copy(
        error = null,
    )

    AddressAction.DismissMessage -> state.copy(
        message = null,
    )

    else -> state
}
