package com.iti.pocketshop.features.address.domain.error

import android.content.Context
import com.iti.pocketshop.network.Error
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.toUserMessage
import com.iti.pocketshop.R

sealed interface AddressError : Error {
    data object MissingCustomerAccessToken : AddressError
    data object MissingMapsApiKey : AddressError
    data object LocationNotFound : AddressError
    data object LocationPermissionDenied : AddressError
    data object CurrentLocationUnavailable : AddressError
    data class Remote(val error: PocketDataError.Remote) : AddressError
    data class MapsService(
        val remoteError: PocketDataError.Remote? = null,
        val detailMessage: String? = null,
    ) : AddressError
    data class Shopify(val messages: List<String>) : AddressError
}

fun AddressError.toUiMessage(context: Context): String = when (this) {
    AddressError.MissingCustomerAccessToken ->
        context.getString(R.string.address_error_missing_customer_access_token)

    AddressError.MissingMapsApiKey ->
        context.getString(R.string.address_error_missing_maps_api_key)

    AddressError.LocationNotFound ->
        context.getString(R.string.address_error_location_not_found)

    AddressError.LocationPermissionDenied ->
        context.getString(R.string.address_error_location_permission_denied)

    AddressError.CurrentLocationUnavailable ->
        context.getString(R.string.address_error_current_location_unavailable)

    is AddressError.Remote ->
        error.toUserMessage(context)

    is AddressError.MapsService -> {
        val baseMessage = remoteError?.toUserMessage(context)
            ?: context.getString(R.string.address_error_maps_service)
        val detail = detailMessage?.trim().orEmpty()
        if (detail.isBlank()) {
            baseMessage
        } else {
            "$baseMessage: $detail"
        }
    }

    is AddressError.Shopify ->
        messages.firstOrNull()?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.address_error_save_failed)
}
