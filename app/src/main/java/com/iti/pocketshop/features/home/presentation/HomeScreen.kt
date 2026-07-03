package com.iti.pocketshop.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.R
import com.iti.pocketshop.core.components.DeleteFavoriteDialogController
import com.iti.pocketshop.core.components.RemoveFavoriteDialog
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.features.home.domain.models.toFavoriteProduct
import com.iti.pocketshop.features.home.presentation.components.BrandRow
import com.iti.pocketshop.features.home.presentation.components.EmptyHome
import com.iti.pocketshop.features.home.presentation.components.HeroBanner
import com.iti.pocketshop.features.home.presentation.components.HomeTopBar
import com.iti.pocketshop.features.home.presentation.components.ProductRow
import com.iti.pocketshop.features.home.presentation.components.SectionHeader
import com.iti.pocketshop.features.productlist.presentation.ProductListRouteInfo
import kotlinx.coroutines.launch

@Composable
fun HomeRoot(
    openSearch: () -> Unit,
    openCategories: () -> Unit,
    openProductList: (ProductListRouteInfo) -> Unit,
    openProductDetails: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        openSearch = openSearch,
        openCategories = openCategories,
        openProductList = openProductList,
        openProductDetails = openProductDetails,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun HomeScreen(
    openSearch: () -> Unit,
    openCategories: () -> Unit,
    openProductList: (ProductListRouteInfo) -> Unit,
    openProductDetails: (String) -> Unit,
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    val isEmptyState =
        state.brands.isEmpty() &&
                state.featuredProducts.isEmpty()
    val user = LocalUser.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTopBar(
            onSearchClick = openSearch
        )
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = {
                onAction(HomeAction.FetchData)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Hero banner
                item {
                    if (!isEmptyState) {
                        HeroBanner(
                            openSales = {
                                //TODO()
                            }
                        )
                    }
                }

                // Categories
                if (state.brands.isNotEmpty()) {
                    item { Spacer(Modifier.height(24.dp)) }
                    item {
                        SectionHeader(
                            title = stringResource(R.string.shop_by_brand),
                            onSeeAllClick = openCategories,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        BrandRow(
                            categories = state.brands,
                            onCategoryClick = { brand ->
                                openProductList(ProductListRouteInfo.Brands(brand.title))
                            }
                        )
                    }
                }

                // Featured products
                if (state.featuredProducts.isNotEmpty()) {
                    item { Spacer(Modifier.height(28.dp)) }
                    item {
                        SectionHeader(
                            title = stringResource(R.string.featured),
                            onSeeAllClick = {
                                openProductList(ProductListRouteInfo.Featured)
                            },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        ProductRow(
                            products = state.featuredProducts,
                            onProductClick = openProductDetails,
                            favoriteIds = state.favoriteIds,
                            onWishlistClick = { product ->
                                if (user?.isAnonymous == true) {
                                    scope.launch {
                                        SignInDialogController.sendEvent(true)
                                    }
                                } else if (state.favoriteIds.contains(product.id)) {
                                    scope.launch {
                                        DeleteFavoriteDialogController.sendEvent(product.toFavoriteProduct())
                                    }
                                } else {
                                    onAction(HomeAction.ToggleFavorite(product.toFavoriteProduct()))
                                }
                            }
                        )
                    }
                }

                // Best sellers
                if (state.bestSellers.isNotEmpty()) {
                    item { Spacer(Modifier.height(28.dp)) }
                    item {
                        SectionHeader(
                            title = stringResource(R.string.trending),
                            badge = stringResource(R.string.on_fire_emoji),
                            onSeeAllClick = {
                                openProductList(ProductListRouteInfo.Trending)
                            },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        ProductRow(
                            products = state.bestSellers,
                            onProductClick = openProductDetails,
                            favoriteIds = state.favoriteIds,
                            onWishlistClick = { product ->
                                if (user?.isAnonymous == true) {
                                    scope.launch {
                                        SignInDialogController.sendEvent(true)
                                    }
                                } else if (state.favoriteIds.contains(product.id)) {
                                    scope.launch {
                                        DeleteFavoriteDialogController.sendEvent(product.toFavoriteProduct())
                                    }
                                } else {
                                    onAction(HomeAction.ToggleFavorite(product.toFavoriteProduct()))
                                }
                            }
                        )
                    }
                }

                // New arrivals
                if (state.newArrivals.isNotEmpty()) {
                    item { Spacer(Modifier.height(28.dp)) }
                    item {
                        SectionHeader(
                            title = stringResource(R.string.new_arrivals),
                            badge = stringResource(R.string.new_items),
                            onSeeAllClick = {
                                openProductList(ProductListRouteInfo.NewArrivals)
                            },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        ProductRow(
                            products = state.newArrivals,
                            onProductClick = openProductDetails,
                            cardWidth = 160.dp,
                            favoriteIds = state.favoriteIds,
                            onWishlistClick = { product ->
                                if (user?.isAnonymous == true) {
                                    scope.launch {
                                        SignInDialogController.sendEvent(true)
                                    }
                                } else if (state.favoriteIds.contains(product.id)) {
                                    scope.launch {
                                        DeleteFavoriteDialogController.sendEvent(product.toFavoriteProduct())
                                    }
                                } else {
                                    onAction(HomeAction.ToggleFavorite(product.toFavoriteProduct()))
                                }
                            }
                        )
                    }
                }

                // Empty state
                if (isEmptyState) {
                    item { EmptyHome(onRefresh = { onAction(HomeAction.FetchData) }) }
                }
            }
        }
    }
    RemoveFavoriteDialog(
        onConfirm = {
            onAction(HomeAction.ToggleFavorite(it))
        }
    )
}
