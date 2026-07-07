package com.iti.pocketshop.features.productdetails.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError

sealed interface ProductDetailsEvent {
    data class ShowError(val error: PocketDataError) : ProductDetailsEvent
}
