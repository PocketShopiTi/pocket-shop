package com.iti.pocketshop.features.search.presentation.action

import com.iti.pocketshop.core.networkutils.PocketDataError

sealed interface SearchEffect {
    data class NavigateToProductDetails(val id: String) : SearchEffect
    object NavigateToFilters : SearchEffect
    data class ShowError(val error: PocketDataError) : SearchEffect
}