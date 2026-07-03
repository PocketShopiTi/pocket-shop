package com.iti.pocketshop.features.search.presentation.action

import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.SortOption

sealed class SearchAction {
    data class UpdateQuery(val query: String) : SearchAction()
    object SubmitSearch : SearchAction()
    object ClearSearch : SearchAction()

    data class ToggleFilter(val filterValue: ProductFilterValue) : SearchAction()
    object ClearFilters : SearchAction()

    data class SelectSortOption(val option: SortOption) : SearchAction()
    data class UpdatePriceRange(val range: ClosedFloatingPointRange<Float>) : SearchAction()
    object ClearPriceRange : SearchAction()

    object OpenFiltersScreen : SearchAction()

    object LoadNextPage : SearchAction()

    data class ClickProduct(val id: String) : SearchAction()
    data class ClickCollection(val id: String) : SearchAction()
    data class ClickArticle(val id: String) : SearchAction()
    data class ClickPage(val id: String) : SearchAction()
    data class ClickQuerySuggestion(val queryText: String) : SearchAction()

    object ClickBrowseCategories : SearchAction()

    object BackClicked : SearchAction()
    object ClearError : SearchAction()
}