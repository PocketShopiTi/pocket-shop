package com.iti.pocketshop.features.search.presentation.action

import com.iti.pocketshop.core.networkutils.PocketDataError

sealed class SearchEffect {
    data class NavigateToProductDetails(val id: String) : SearchEffect()
    data class NavigateToCollection(val id: String) : SearchEffect()
    data class NavigateToArticle(val id: String) : SearchEffect()
    data class NavigateToPage(val id: String) : SearchEffect()
    object NavigateToFilters : SearchEffect()
    object NavigateBack : SearchEffect()

    data class ShowError(val error: PocketDataError) : SearchEffect()
}