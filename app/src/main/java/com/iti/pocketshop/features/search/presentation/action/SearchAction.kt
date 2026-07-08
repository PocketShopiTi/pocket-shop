package com.iti.pocketshop.features.search.presentation.action

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.SortOption

sealed interface SearchAction {
    data class UpdateQuery(val query: String) : SearchAction
    object SubmitSearch : SearchAction
    object ClearSearch : SearchAction

    data class ToggleFilter(val filterValue: ProductFilterValue) : SearchAction
    object ClearFilters : SearchAction

    // Used by the Filters screen (deferred — applied only when SubmitSearch fires)
    data class SelectSortOption(val option: SortOption) : SearchAction
    data class UpdatePriceRange(val range: ClosedFloatingPointRange<Float>) : SearchAction
    object ClearPriceRange : SearchAction

    // Used by the inline quick-edit sheets opened from filter chips on the Search page
    // (applied immediately — updates state and triggers a search right away)
    data class QuickToggleFilter(val filterValue: ProductFilterValue) : SearchAction
    data class QuickUpdatePriceRange(val range: ClosedFloatingPointRange<Float>) : SearchAction
    object QuickClearPriceRange : SearchAction

    object OpenFiltersScreen : SearchAction

    object LoadNextPage : SearchAction

    data class ClickProduct(val id: String) : SearchAction
    data class ClickQuerySuggestion(val queryText: String) : SearchAction

    object ClickBrowseCategories : SearchAction

    object ClearError : SearchAction

    data class ToggleFavorite(val product: FavoriteProduct) : SearchAction
}
