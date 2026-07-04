package com.iti.pocketshop.features.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.search.domain.model.ProductFilterValue
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResultItem
import com.iti.pocketshop.features.search.domain.model.SortOption
import com.iti.pocketshop.features.search.domain.model.extractPriceRangeBounds
import com.iti.pocketshop.features.search.domain.model.hasSameSelectionAs
import com.iti.pocketshop.features.search.domain.usecase.GetPredictiveSearchUseCase
import com.iti.pocketshop.features.search.domain.usecase.GetSearchResultsUseCase
import com.iti.pocketshop.features.search.presentation.action.SearchAction
import com.iti.pocketshop.features.search.presentation.action.SearchEffect
import com.iti.pocketshop.features.search.presentation.state.SearchPhase
import com.iti.pocketshop.features.search.presentation.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getPredictiveSearchUseCase: GetPredictiveSearchUseCase,
    private val getSearchResultsUseCase: GetSearchResultsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var predictiveJob: Job? = null

    private val _effect = Channel<SearchEffect>(Channel.BUFFERED)
    val effect: Flow<SearchEffect> = _effect.receiveAsFlow()

    private fun sendEffect(effect: SearchEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    init {
        loadInitialProducts()
    }

    private fun loadInitialProducts() {
        viewModelScope.launch {
            when (val result = getSearchResultsUseCase(query = "")) {
                is PocketResult.Success -> {
                    _state.update { it.copy(initialProducts = result.data.products) }
                }

                is PocketResult.Error -> Unit
            }
        }
    }

    fun processIntent(intent: SearchAction) {
        when (intent) {
            is SearchAction.UpdateQuery -> updateQuery(intent.query)
            is SearchAction.SubmitSearch -> submitSearch()
            is SearchAction.ClearSearch -> clearSearch()
            is SearchAction.ToggleFilter -> toggleFilter(intent.filterValue)
            is SearchAction.ClearFilters -> clearFilters()
            is SearchAction.QuickToggleFilter -> quickToggleFilter(intent.filterValue)
            is SearchAction.QuickUpdatePriceRange -> quickUpdatePriceRange(intent.range)
            SearchAction.QuickClearPriceRange -> quickClearPriceRange()
            is SearchAction.LoadNextPage -> loadNextPage()
            is SearchAction.ClickQuerySuggestion -> queryExtracted(intent)
            is SearchAction.ClickBrowseCategories -> clearSearch()
            is SearchAction.SelectSortOption -> selectSortOption(intent.option)
            is SearchAction.UpdatePriceRange -> updatePriceRange(intent.range)
            is SearchAction.ClearPriceRange -> clearPriceRange()
            SearchAction.ClearError -> clearError()
            is SearchAction.ClickProduct -> onClickProduct(intent)
            is SearchAction.ClickCollection -> onClickCollection(intent)
            is SearchAction.ClickArticle -> onClickArticle(intent)
            is SearchAction.ClickPage -> onClickPage(intent)
            SearchAction.OpenFiltersScreen -> onOpenFilterScreen()
            SearchAction.BackClicked -> onBackClicked()
        }
    }

    private fun onBackClicked() {
        sendEffect(SearchEffect.NavigateBack)
    }

    private fun onOpenFilterScreen() {
        sendEffect(SearchEffect.NavigateToFilters)
    }

    private fun onClickPage(intent: SearchAction.ClickPage) {
        sendEffect(SearchEffect.NavigateToPage(intent.id))
    }

    private fun onClickArticle(intent: SearchAction.ClickArticle) {
        sendEffect(SearchEffect.NavigateToArticle(intent.id))
    }

    private fun onClickCollection(intent: SearchAction.ClickCollection) {
        sendEffect(SearchEffect.NavigateToCollection(intent.id))
    }

    private fun onClickProduct(intent: SearchAction.ClickProduct) {
        sendEffect(SearchEffect.NavigateToProductDetails(intent.id))
    }

    private fun selectSortOption(option: SortOption) {
        _state.update { it.copy(activeSortOption = option) }
    }

    private fun updatePriceRange(range: ClosedFloatingPointRange<Float>) {
        _state.update { it.copy(activePriceRange = range) }
    }

    private fun clearPriceRange() {
        _state.update { it.copy(activePriceRange = null) }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun queryExtracted(intent: SearchAction.ClickQuerySuggestion) {
        _state.update { it.copy(query = intent.queryText) }
        submitSearch()
    }

    private fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
        if (query.isBlank()) {
            predictiveJob?.cancel()
            _state.update { it.copy(phase = SearchPhase.Initial, lastPredictiveResult = null) }
            return
        }
        predictiveJob?.cancel()
        predictiveJob = viewModelScope.launch {
            when (val result = getPredictiveSearchUseCase(query)) {
                is PocketResult.Success -> _state.update {
                    it.copy(
                        phase = SearchPhase.Predictive(result.data),
                        lastPredictiveResult = result.data,
                        isLoading = false
                    )
                }

                is PocketResult.Error -> {
                    sendEffect(SearchEffect.ShowError(result.error))
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }


    private fun submitSearch() {
        val currentQuery = _state.value.query
        if (currentQuery.isBlank()) return
        val predictiveFallback = _state.value.lastPredictiveResult

        predictiveJob?.cancel()
        predictiveJob = null

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val finalFilters = buildFilterInputs()
            val (sortKey, reverse) = currentSortParams()

            when (val result = getSearchResultsUseCase(
                query = currentQuery, filters = finalFilters, sortKey = sortKey, reverse = reverse
            )) {
                is PocketResult.Success -> {
                    val newBounds = _state.value.priceRangeBounds
                        ?: result.data.filters.extractPriceRangeBounds()

                    val newPhase = when {
                        result.data.products.isNotEmpty() -> SearchPhase.Results(result.data)
                        else -> {
                            val predictiveItems = predictiveFallback?.products?.map { p ->
                                    SearchResultItem.ProductItem(
                                        id = p.id,
                                        title = p.title,
                                        handle = p.handle,
                                        imageUrl = p.imageUrl,
                                        imageAlt = p.imageAlt,
                                        price = p.price,
                                        currencyCode = p.currencyCode,
                                        vendor = "",
                                        productType = "",
                                        tags = emptyList(),
                                        options = emptyList()
                                    )
                                }.orEmpty()

                            if (predictiveItems.isNotEmpty()) {
                                SearchPhase.Results(
                                    SearchResult(
                                        items = predictiveItems,
                                        totalCount = predictiveItems.size,
                                        hasNextPage = false,
                                        endCursor = null,
                                        filters = emptyList()
                                    )
                                )
                            } else {
                                SearchPhase.Empty
                            }
                        }
                    }

                    _state.update {
                        it.copy(
                            phase = newPhase,
                            isLoading = false,
                            priceRangeBounds = newBounds,
                        )
                    }
                }

                is PocketResult.Error -> {
                    sendEffect(SearchEffect.ShowError(result.error))
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun clearSearch() {
        predictiveJob?.cancel()
        predictiveJob = null
        _state.update {
            it.copy(
                query = "",
                phase = SearchPhase.Initial,
                error = null,
                activeFilters = emptyList(),
                activePriceRange = null,
                priceRangeBounds = null,
                lastPredictiveResult = null,
            )
        }
    }

    private fun toggleFilter(filter: ProductFilterValue) {
        _state.update { currentState ->
            val currentFilters = currentState.activeFilters.toMutableList()
            val existingIndex = currentFilters.indexOfFirst { it.hasSameSelectionAs(filter) }
            if (existingIndex >= 0) currentFilters.removeAt(existingIndex)
            else currentFilters.add(filter)
            currentState.copy(activeFilters = currentFilters)
        }
    }

    private fun quickToggleFilter(filter: ProductFilterValue) {
        toggleFilter(filter)
        submitSearch()
    }

    private fun quickUpdatePriceRange(range: ClosedFloatingPointRange<Float>) {
        _state.update { it.copy(activePriceRange = range) }
        submitSearch()
    }

    private fun quickClearPriceRange() {
        _state.update { it.copy(activePriceRange = null) }
        submitSearch()
    }

    private fun clearFilters() {
        _state.update {
            it.copy(
                activeFilters = emptyList(),
                activePriceRange = null,
                activeSortOption = SortOption.RELEVANCE,
            )
        }
    }

    private fun loadNextPage() {
        val currentPhase = _state.value.phase
        if (currentPhase !is SearchPhase.Results) return
        if (!currentPhase.searchResult.hasNextPage) return

        viewModelScope.launch {
            val finalFilters = buildFilterInputs()
            val (sortKey, reverse) = currentSortParams()

            when (val result = getSearchResultsUseCase(
                query = _state.value.query,
                after = currentPhase.searchResult.endCursor,
                filters = finalFilters,
                sortKey = sortKey,
                reverse = reverse
            )) {
                is PocketResult.Success -> {
                    val updatedItems = currentPhase.searchResult.items + result.data.items
                    val updatedResult = result.data.copy(items = updatedItems)
                    _state.update { it.copy(phase = SearchPhase.Results(updatedResult)) }
                }

                is PocketResult.Error -> {
                    sendEffect(SearchEffect.ShowError(result.error))
                }
            }
        }
    }

    private fun buildFilterInputs(): List<String>? {
        val filterInputs = _state.value.activeFilters.map { it.input }.toMutableList()
        _state.value.activePriceRange?.let { range ->
            filterInputs.add("{\"price\":{\"min\":${range.start},\"max\":${range.endInclusive}}}")
        }
        return filterInputs.ifEmpty { null }
    }

    private fun currentSortParams(): Pair<String, Boolean?> = when (_state.value.activeSortOption) {
        SortOption.RELEVANCE -> "RELEVANCE" to null
        SortOption.PRICE_LOW_TO_HIGH -> "PRICE" to false
        SortOption.PRICE_HIGH_TO_LOW -> "PRICE" to true
    }
}