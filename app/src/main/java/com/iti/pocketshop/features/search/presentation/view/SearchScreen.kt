package com.iti.pocketshop.features.search.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.core.components.RemoveFavoriteDialog
import com.iti.pocketshop.features.productlist.presentation.ProductListAction
import com.iti.pocketshop.features.search.presentation.action.SearchAction
import com.iti.pocketshop.features.search.presentation.action.SearchEffect
import com.iti.pocketshop.features.search.presentation.state.SearchPhase
import com.iti.pocketshop.features.search.presentation.state.SearchState
import com.iti.pocketshop.features.search.presentation.viewmodel.SearchViewModel
import com.iti.pocketshop.features.search.presentation.view.components.FilterChipsRow
import com.iti.pocketshop.features.search.presentation.view.components.PredictiveSearchContent
import com.iti.pocketshop.features.search.presentation.view.components.SearchBar
import com.iti.pocketshop.features.search.presentation.view.components.SearchEmptyState
import com.iti.pocketshop.features.search.presentation.view.components.SearchInitialContent
import com.iti.pocketshop.features.search.presentation.view.components.SearchResultsContent

@Composable
fun SearchRoot(
    viewModel: SearchViewModel,
    onBack: () -> Unit,
    openProductDetails: (String) -> Unit,
    onOpenFiltersScreen: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToProductDetails -> openProductDetails(effect.id)
                is SearchEffect.NavigateToCollection -> Unit
                is SearchEffect.NavigateToArticle -> Unit
                is SearchEffect.NavigateToPage -> Unit
                SearchEffect.NavigateToFilters -> onOpenFiltersScreen()
                SearchEffect.NavigateBack -> onBack()
                is SearchEffect.ShowError -> snackbarHostState.showSnackbar(effect.error.toString())
            }
        }
    }

    SearchScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::processIntent,
    )

    RemoveFavoriteDialog(
        onConfirm = {
            viewModel.processIntent(SearchAction.ToggleFavorite(it))
        }
    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    snackbarHostState: SnackbarHostState,
    onAction: (SearchAction) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.search_explore_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )

                SearchBar(
                    query = state.query,
                    onAction = onAction,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Crossfade(
                    targetState = state.phase,
                    label = "search_phase_transition",
                ) { phase ->
                    when (phase) {
                        SearchPhase.Initial -> {
                            SearchInitialContent(
                                products = state.initialProducts,
                                onAction = onAction,
                            )
                        }

                        is SearchPhase.Predictive -> {
                            PredictiveSearchContent(
                                predictiveResult = phase.predictiveResult,
                                onAction = onAction,
                            )
                        }

                        is SearchPhase.Results -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                FilterChipsRow(
                                    filterGroups = phase.searchResult.filters,
                                    activeFilters = state.activeFilters,
                                    activeSortOption = state.activeSortOption,
                                    activePriceRange = state.activePriceRange,
                                    priceRangeBounds = state.priceRangeBounds,
                                    onAction = onAction,
                                    onOpenFiltersScreen = { onAction(SearchAction.OpenFiltersScreen) },
                                )
                                SearchResultsContent(
                                    query = state.query,
                                    searchResult = phase.searchResult,
                                    favoriteIds = state.favoriteIds,
                                    isLoadingNextPage = state.isLoadingNextPage,
                                    onAction = onAction,
                                )
                            }
                        }

                        SearchPhase.Empty -> SearchEmptyState(onAction = onAction)
                    }
                }
            }

            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                CircularProgressIndicator()
            }
        }
    }
}