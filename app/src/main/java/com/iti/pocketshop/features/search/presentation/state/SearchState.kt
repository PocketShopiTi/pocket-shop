package com.iti.pocketshop.features.search.presentation.state

import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.domain.model.SortOption

data class SearchState(
    val query: String = "",
    val phase: SearchPhase = SearchPhase.Initial,
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeFilters: List<ProductFilterValue> = emptyList(),
    val initialProducts: List<SearchResultItem.ProductItem> = emptyList(),
    val activeSortOption: SortOption = SortOption.RELEVANCE,
    val priceRangeBounds: ClosedFloatingPointRange<Float>? = null,
    val activePriceRange: ClosedFloatingPointRange<Float>? = null,
)

sealed class SearchPhase {
    object Initial : SearchPhase()

    data class Predictive(
        val predictiveResult: PredictiveSearchResult
    ) : SearchPhase()

    data class Results(
        val searchResult: SearchResult
    ) : SearchPhase()

    object Empty : SearchPhase()
}