package com.iti.pocketshop.features.search.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.features.search.presentation.action.SearchEffect
import com.iti.pocketshop.features.search.presentation.state.SearchPhase
import com.iti.pocketshop.features.search.presentation.viewmodel.SearchViewModel


@Composable
fun FiltersRoot(
    viewModel: SearchViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is SearchEffect.NavigateBack) onBack()
        }
    }

    val filterGroups = (state.phase as? SearchPhase.Results)?.searchResult?.filters ?: emptyList()
    val resultsCount = (state.phase as? SearchPhase.Results)?.searchResult?.totalCount ?: 0

    FiltersScreen(
        filterGroups = filterGroups,
        activeFilters = state.activeFilters,
        activeSortOption = state.activeSortOption,
        priceRangeBounds = state.priceRangeBounds,
        activePriceRange = state.activePriceRange,
        resultsCount = resultsCount,
        onAction = viewModel::processIntent,
    )
}