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

    val filterGroups = (state.phase as? SearchPhase.Results)?.searchResult?.filters ?: emptyList()

    FiltersScreen(
        filterGroups = filterGroups,
        onBack = onBack,
        activeFilters = state.activeFilters,
        activeSortOption = state.activeSortOption,
        priceRangeBounds = state.priceRangeBounds,
        activePriceRange = state.activePriceRange,
        onAction = viewModel::processIntent,
    )
}