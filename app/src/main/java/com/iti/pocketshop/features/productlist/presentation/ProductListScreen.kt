package com.iti.pocketshop.features.productlist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.iti.pocketshop.R
import com.iti.pocketshop.features.home.presentation.components.ProductCard

@Composable
fun ProductListRoot(
    routeInfo: ProductListRouteInfo,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    LaunchedEffect(routeInfo) {
        viewModel.onAction(ProductListAction.UpdateRouteInfo(routeInfo))
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ProductListScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onProductClick = onProductClick
    )
}

@Composable
private fun ProductListScreen(
    state: ProductListState,
    onAction: (ProductListAction) -> Unit,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val gridState = rememberLazyGridState()

    // Trigger load more when reaching near the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 6 && state.hasNextPage && !state.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onAction(ProductListAction.LoadMore)
        }
    }

    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.productListRouteInfo.screenTitle(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                            contentDescription = stringResource(R.string.search),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = isSearchActive
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onAction(ProductListAction.SearchProducts(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_search),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onAction(ProductListAction.SearchProducts("")) }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                                    contentDescription = stringResource(R.string.clear_search)
                                )
                            }
                        }
                    }
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { onAction(ProductListAction.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.filteredProducts.isEmpty() && !state.isLoading) {
                    EmptyProductList(
                        isSearch = state.searchQuery.isNotEmpty(),
                        onRefresh = { onAction(ProductListAction.Refresh) }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        contentPadding = PaddingValues(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                modifier = Modifier.animateItem(),
                                isFavorite = false,
                                onWishlistClick = {
                                    // todo
                                }
                            )
                        }

                        // Loading more indicator
                        if (state.isLoadingMore) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyProductList(
    isSearch: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_empty_list)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(260.dp)
        )
        Text(
            text = stringResource(
                if (isSearch) R.string.no_results_found
                else R.string.nothing_to_show_yet
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(
                if (isSearch) R.string.try_searching_for_something_else
                else R.string.pull_down_to_refresh_or_tap_below
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRefresh,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.refresh),
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(80.dp))
    }
}
