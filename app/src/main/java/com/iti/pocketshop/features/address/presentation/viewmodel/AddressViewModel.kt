package com.iti.pocketshop.features.address.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.LocationCoordinates
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.domain.usecase.GetCurrentLocationUseCase
import com.iti.pocketshop.features.address.domain.usecase.DeleteAddressUseCase
import com.iti.pocketshop.features.address.domain.usecase.GetAddressesUseCase
import com.iti.pocketshop.features.address.domain.usecase.ResolveAddressSuggestionUseCase
import com.iti.pocketshop.features.address.domain.usecase.ReverseGeocodeLocationUseCase
import com.iti.pocketshop.features.address.domain.usecase.SaveAddressUseCase
import com.iti.pocketshop.features.address.domain.usecase.SearchAddressSuggestionsUseCase
import com.iti.pocketshop.features.address.domain.usecase.SetDefaultAddressUseCase
import com.iti.pocketshop.features.address.presentation.action.AddressAction
import com.iti.pocketshop.features.address.presentation.state.AddressEditorState
import com.iti.pocketshop.features.address.presentation.state.AddressState
import com.iti.pocketshop.features.address.utils.AndroidAddressValidationStrings
import com.iti.pocketshop.features.address.utils.validateAddressEditor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase,
    private val searchAddressSuggestionsUseCase: SearchAddressSuggestionsUseCase,
    private val resolveAddressSuggestionUseCase: ResolveAddressSuggestionUseCase,
    private val reverseGeocodeLocationUseCase: ReverseGeocodeLocationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddressState())
    val state: StateFlow<AddressState> = _state.asStateFlow()

    private var loadJob: Job? = null
    private var mutationJob: Job? = null
    private var locationSearchJob: Job? = null
    private var locationResolveJob: Job? = null
    private val validationStrings = AndroidAddressValidationStrings(appContext)

    init {
        refreshAddresses()
    }

    fun onAction(action: AddressAction) {
        when (action) {
            AddressAction.Refresh -> refreshAddresses()
            AddressAction.Retry -> refreshAddresses()
            AddressAction.SaveClicked -> saveCurrentAddress()
            AddressAction.ConfirmDelete -> confirmDelete()
            is AddressAction.SetDefaultClicked -> setDefaultAddress(action.addressId)
            is AddressAction.LocationPermissionResult -> handleLocationPermissionResult(action.granted)
            is AddressAction.LocationSearchChanged -> updateLocationSearch(action.query)
            is AddressAction.LocationSuggestionSelected -> resolveLocationSuggestion(action.suggestion)
            is AddressAction.MapLocationPicked -> reverseGeocodeLocation(
                action.latitude,
                action.longitude
            )

            AddressAction.ClearLocationSuggestions -> onClearLocationSuggestions(action)
            is AddressAction.EditAddressClicked -> {
                onEditAdress(action)
            }

            AddressAction.AddAddressClicked,
            AddressAction.CloseEditor -> onClickAction(action)

            is AddressAction.FieldChanged -> onFieldChanged(action)

            AddressAction.ToggleDefault,
            is AddressAction.PhoneCountryChanged,
            is AddressAction.DeleteClicked,
            AddressAction.CancelDelete,
            AddressAction.DismissError,
            AddressAction.DismissMessage -> onStateOnlyAction(action)
        }
    }

    private fun onEditAdress(action: AddressAction.EditAddressClicked) {
        onClickAction(action)
        val address = _state.value.addresses.firstOrNull { it.id == action.addressId }
        if (address != null && (address.latitude == null || address.longitude == null)) {
            val query = listOf(address.address1, address.city, address.country)
                .filter { it.isNotBlank() }
                .joinToString(", ")
            if (query.isNotBlank()) {
                viewModelScope.launch {
                    when (val result = searchAddressSuggestionsUseCase(query)) {
                        is PocketResult.Success -> {
                            val suggestion = result.data.firstOrNull()
                            if (suggestion != null) {
                                val latLngStr = suggestion.placeId.substringBefore("|")
                                val parts = latLngStr.split(",")
                                if (parts.size >= 2) {
                                    val lat = parts[0].toDoubleOrNull()
                                    val lng = parts[1].toDoubleOrNull()
                                    if (lat != null && lng != null) {
                                        _state.update { current ->
                                            if (current.editor.isEditing && current.editor.addressId == address.id) {
                                                current.copy(
                                                    editor = current.editor.copy(
                                                        latitude = lat,
                                                        longitude = lng,
                                                    )
                                                )
                                            } else {
                                                current
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun onStateOnlyAction(action: AddressAction) {
        _state.update { current ->
            reduceAddressState(current, action)
        }
    }

    private fun onFieldChanged(action: AddressAction.FieldChanged) {
        cancelLocationJobs()
        _state.update { current ->
            reduceAddressState(current, action)
        }
    }

    private fun onClickAction(action: AddressAction) {
        cancelLocationJobs()
        _state.update { current ->
            reduceAddressState(current, action)
        }
    }

    private fun refreshAddresses(silent: Boolean = false) {
        if (loadJob?.isActive == true) {
            return
        }
        if (!silent && _state.value.isSaving) {
            return
        }
        if (!silent) {
            _state.update { current ->
                current.copy(
                    isLoading = true,
                    error = null,
                    message = null,
                )
            }
        }
        loadJob = viewModelScope.launch {
            when (val result = getAddressesUseCase()) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        isLoading = false,
                        error = result.error,
                    )
                }

                is PocketResult.Success -> _state.update { current ->
                    current.copy(
                        isLoading = false,
                        customerName = result.data.customerName,
                        addresses = result.data.addresses,
                        defaultAddressId = result.data.defaultAddressId,
                        pendingDeleteAddressId = null,
                        error = null,
                    )
                }
            }
        }
    }

    private fun saveCurrentAddress() {
        if (_state.value.isLoading || _state.value.isSaving || mutationJob?.isActive == true) {
            return
        }

        val editor = _state.value.editor
        val validationErrors = validateAddressEditor(editor, validationStrings)
        if (validationErrors.isNotEmpty()) {
            _state.update { current ->
                current.copy(
                    editor = editor.withValidationErrors(validationErrors),
                    error = null,
                    message = null,
                )
            }
            return
        }

        cancelLocationJobs()
        _state.update { current ->
            current.copy(
                isSaving = true,
                error = null,
                message = null,
                editor = current.editor.copy(validationErrors = emptyMap()),
            )
        }

        mutationJob = viewModelScope.launch {
            when (val result = saveAddressUseCase(
                addressId = editor.addressId,
                draft = editor.toDraft(),
            )) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        isSaving = false,
                        error = result.error,
                    )
                }

                is PocketResult.Success -> {
                    _state.update { current ->
                        current.copy(
                            isSaving = false,
                            editor = AddressEditorState(),
                            pendingDeleteAddressId = null,
                            error = null,
                            message = if (editor.isEditing) {
                                appContext.getString(R.string.address_message_updated_successfully)
                            } else {
                                appContext.getString(R.string.address_message_added_successfully)
                            },
                        )
                    }
                    refreshAddresses(silent = true)
                }
            }
        }
    }

    private fun confirmDelete() {
        val addressId = _state.value.pendingDeleteAddressId ?: return
        if (_state.value.isLoading || _state.value.isSaving || mutationJob?.isActive == true) {
            return
        }
        _state.update { current ->
            current.copy(
                isSaving = true,
                error = null,
                message = null,
            )
        }
        mutationJob = viewModelScope.launch {
            when (val result = deleteAddressUseCase(addressId)) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        isSaving = false,
                        error = result.error,
                    )
                }

                is PocketResult.Success -> {
                    _state.update { current ->
                        current.copy(
                            isSaving = false,
                            message = appContext.getString(R.string.address_message_deleted),
                            pendingDeleteAddressId = null,
                            error = null,
                        )
                    }
                    refreshAddresses(silent = true)
                }
            }
        }
    }

    private fun setDefaultAddress(addressId: String) {
        if (_state.value.isLoading || _state.value.isSaving || mutationJob?.isActive == true) {
            return
        }
        _state.update { current ->
            current.copy(
                isSaving = true,
                error = null,
                message = null,
            )
        }
        mutationJob = viewModelScope.launch {
            when (val result = setDefaultAddressUseCase(addressId)) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        isSaving = false,
                        error = result.error,
                    )
                }

                is PocketResult.Success -> {
                    _state.update { current ->
                        current.copy(
                            isSaving = false,
                            message = appContext.getString(R.string.address_message_default_updated),
                            error = null,
                        )
                    }
                    refreshAddresses(silent = true)
                }
            }
        }
    }

    private fun handleLocationPermissionResult(granted: Boolean) {
        val editor = _state.value.editor
        val hasSavedLocation = editor.latitude != null && editor.longitude != null
        if (!editor.visible || hasSavedLocation || editor.isEditing) {
            return
        }

        if (!granted) {
            cancelLocationJobs()
            _state.update { current ->
                current.copy(
                    error = AddressError.LocationPermissionDenied,
                )
            }
            return
        }

        loadCurrentLocation()
    }

    private fun loadCurrentLocation() {
        if (_state.value.isLoading || _state.value.isSaving) {
            return
        }

        cancelLocationJobs()
        _state.update { current ->
            current.copy(
                editor = current.editor.copy(
                    isLocationResolving = true,
                    locationSuggestions = emptyList(),
                ),
                error = null,
                message = null,
            )
        }

        locationResolveJob = viewModelScope.launch {
            when (val locationResult = getCurrentLocationUseCase()) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        editor = current.editor.copy(isLocationResolving = false),
                        error = locationResult.error,
                    )
                }

                is PocketResult.Success -> resolveCurrentLocation(locationResult.data)
            }
        }
    }

    private suspend fun resolveCurrentLocation(coordinates: LocationCoordinates) {
        _state.update { current ->
            current.copy(
                editor = current.editor
                    .withCoordinates(coordinates.latitude, coordinates.longitude)
                    .copy(isLocationResolving = true),
            )
        }

        when (val result = reverseGeocodeLocationUseCase(
            coordinates.latitude,
            coordinates.longitude,
        )) {
            is PocketResult.Error -> _state.update { current ->
                current.copy(
                    editor = current.editor.copy(isLocationResolving = false),
                    error = result.error,
                )
            }

            is PocketResult.Success -> _state.update { current ->
                current.copy(
                    editor = current.editor.withLocationSelection(result.data),
                    error = null,
                )
            }
        }
    }

    private fun updateLocationSearch(query: String) {
        if (_state.value.isLoading || _state.value.isSaving) {
            return
        }

        cancelLocationJobs()
        _state.update { current ->
            reduceAddressState(current, AddressAction.LocationSearchChanged(query))
        }

        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 3) {
            _state.update { current ->
                current.copy(
                    editor = current.editor.copy(
                        isLocationSearching = false,
                        locationSuggestions = emptyList(),
                    ),
                )
            }
            return
        }

        _state.update { current ->
            current.copy(
                editor = current.editor.copy(isLocationSearching = true),
            )
        }

        locationSearchJob = viewModelScope.launch {
            delay(300)
            when (val result = searchAddressSuggestionsUseCase(trimmedQuery)) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        editor = current.editor.copy(
                            isLocationSearching = false,
                            locationSuggestions = emptyList(),
                        ),
                        error = result.error,
                    )
                }

                is PocketResult.Success -> _state.update { current ->
                    current.copy(
                        editor = current.editor.withLocationSuggestions(result.data),
                    )
                }
            }
        }
    }

    private fun onClearLocationSuggestions(action: AddressAction) {
        _state.update { current ->
            reduceAddressState(current, action)
        }
    }

    private fun resolveLocationSuggestion(suggestion: AddressLocationSuggestion) {
        if (_state.value.isLoading || _state.value.isSaving) {
            return
        }

        cancelLocationJobs()
        _state.update { current ->
            current.copy(
                editor = current.editor.copy(
                    locationSearchQuery = suggestion.primaryText.ifBlank { suggestion.secondaryText }
                        .ifBlank { suggestion.placeId },
                    isLocationResolving = true,
                    locationSuggestions = emptyList(),
                ),
                error = null,
                message = null,
            )
        }

        locationResolveJob = viewModelScope.launch {
            when (val result = resolveAddressSuggestionUseCase(suggestion.placeId)) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        editor = current.editor.copy(isLocationResolving = false),
                        error = result.error,
                    )
                }

                is PocketResult.Success -> _state.update { current ->
                    current.copy(
                        editor = current.editor.withLocationSelection(
                            result.data,
                            updateSearchQuery = true,
                        ),
                        error = null,
                    )
                }
            }
        }
    }

    private fun reverseGeocodeLocation(latitude: Double, longitude: Double) {
        if (_state.value.isLoading || _state.value.isSaving) {
            return
        }

        cancelLocationJobs()
        _state.update { current ->
            current.copy(
                editor = current.editor
                    .withCoordinates(latitude, longitude)
                    .copy(
                        isLocationResolving = true,
                        locationSuggestions = emptyList(),
                    ),
                error = null,
                message = null,
            )
        }

        locationResolveJob = viewModelScope.launch {
            when (val result = reverseGeocodeLocationUseCase(latitude, longitude)) {
                is PocketResult.Error -> _state.update { current ->
                    current.copy(
                        editor = current.editor.copy(isLocationResolving = false),
                        error = result.error,
                    )
                }

                is PocketResult.Success -> _state.update { current ->
                    current.copy(
                        editor = current.editor.withLocationSelection(result.data),
                        error = null,
                    )
                }
            }
        }
    }

    private fun cancelLocationJobs() {
        locationSearchJob?.cancel()
        locationSearchJob = null
        locationResolveJob?.cancel()
        locationResolveJob = null
        _state.update { current ->
            current.copy(
                editor = current.editor.copy(
                    isLocationSearching = false,
                    isLocationResolving = false,
                    locationSuggestions = emptyList(),
                    hasSearchResult = false,
                ),
            )
        }
    }
}