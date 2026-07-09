package com.iti.pocketshop.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import com.iti.pocketshop.features.home.presentation.components.CategoriesRow
import com.iti.pocketshop.features.home.presentation.components.CouponOfferScreen
import com.iti.pocketshop.features.home.presentation.components.EmptyHome
import com.iti.pocketshop.features.home.presentation.components.HeroBanner
import com.iti.pocketshop.features.home.presentation.components.HomeShimmer
import com.iti.pocketshop.features.home.presentation.components.HomeTopBar
import com.iti.pocketshop.features.home.presentation.components.ProductRow
import com.iti.pocketshop.features.home.presentation.components.SectionHeader
import com.iti.pocketshop.features.home.presentation.models.UIProduct
import com.iti.pocketshop.features.productlist.presentation.ProductListRouteInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeRoot(
    openSearch: () -> Unit,
    openBrands: () -> Unit,
    openProductList: (ProductListRouteInfo) -> Unit,
    openProductDetails: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = LocalUser.current
    val scope: CoroutineScope = rememberCoroutineScope()

    HomeScreen(
        openSearch = openSearch,
        openBrands = openBrands,
        openProductList = openProductList,
        openProductDetails = openProductDetails,
        state = state,
        onAction = viewModel::onAction,
        onWishlistClick = { uiProduct ->
            val product = uiProduct.originalProduct
            if (user?.isAnonymous == true) {
                scope.launch {
                    SignInDialogController.sendEvent(true)
                }
            } else if (state.favoriteIds.contains(product.id)) {
                scope.launch {
                    DeleteFavoriteDialogController.sendEvent(product.toFavoriteProduct())
                }
            } else {
                viewModel.onAction(HomeAction.ToggleFavorite(product.toFavoriteProduct()))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreen(
    openSearch: () -> Unit,
    openBrands: () -> Unit,
    openProductList: (ProductListRouteInfo) -> Unit,
    openProductDetails: (String) -> Unit,
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onWishlistClick: (UIProduct) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTopBar(
            onSearchClick = openSearch,
        )
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = state.isLoading && !state.isEmptyState,
            onRefresh = {
                onAction(HomeAction.FetchData)
            },
            indicator = {
                LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = state.isLoading && !state.isEmptyState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
        ) {
            if (state.isLoading && state.isEmptyState) {
                HomeShimmer()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Hero banner
                    if (!state.isEmptyState) {
                        item(key = "hero_banner") {
                            HeroBanner(
                                ads = state.promotionAds,
                                onAdClick = { ad -> onAction(HomeAction.OpenPromotionAd(ad)) },
                            )
                        }
                    }

                    // Brands
                    if (state.brands.isNotEmpty()) {
                        item(key = "brands_spacer") { Spacer(Modifier.height(16.dp)) }
                        item(key = "brands_header") {
                            SectionHeader(
                                title = stringResource(R.string.shop_by_brand),
                                onSeeAllClick = openBrands,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        item(key = "brands_spacer_2") { Spacer(Modifier.height(8.dp)) }
                        item(key = "brands_row") {
                            BrandRow(
                                brands = state.brands,
                                onBrandClick = { brand ->
                                    openProductList(ProductListRouteInfo.Brands(brand.brandName))
                                }
                            )
                        }
                    }

                    // Categories
                    if (state.categories.isNotEmpty()) {
                        item(key = "categories_spacer") { Spacer(Modifier.height(16.dp)) }
                        item(key = "categories_header") {
                            SectionHeader(
                                title = stringResource(R.string.all_categories),
                                onSeeAllClick = null,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        item(key = "categories_spacer_2") { Spacer(Modifier.height(8.dp)) }
                        item(key = "categories_row") {
                            CategoriesRow(
                                categories = state.categories,
                                onCategoryClick = { category ->
                                    openProductList(ProductListRouteInfo.Category(category.catName))
                                }
                            )
                        }
                    }


                    // New arrivals
                    if (state.newArrivals.isNotEmpty()) {
                        item(key = "new_arrivals_spacer") { Spacer(Modifier.height(20.dp)) }
                        item(key = "new_arrivals_header") {
                            SectionHeader(
                                title = stringResource(R.string.new_arrivals),
                                badge = stringResource(R.string.new_items),
                                onSeeAllClick = {
                                    openProductList(ProductListRouteInfo.NewArrivals)
                                },
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        item(key = "new_arrivals_spacer_2") { Spacer(Modifier.height(12.dp)) }
                        item(key = "new_arrivals_row") {
                            ProductRow(
                                products = state.newArrivals,
                                onProductClick = openProductDetails,
                                onWishlistClick = onWishlistClick
                            )
                        }
                    }

                    // Best sellers
                    if (state.bestSellers.isNotEmpty()) {
                        item(key = "trending_spacer") { Spacer(Modifier.height(20.dp)) }
                        item(key = "trending_header") {
                            SectionHeader(
                                title = stringResource(R.string.trending),
                                badge = stringResource(R.string.on_fire_emoji),
                                onSeeAllClick = {
                                    openProductList(ProductListRouteInfo.Trending)
                                },
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        item(key = "trending_spacer_2") { Spacer(Modifier.height(12.dp)) }
                        item(key = "trending_row") {
                            ProductRow(
                                products = state.bestSellers,
                                onProductClick = openProductDetails,
                                onWishlistClick = onWishlistClick
                            )
                        }
                    }

                    // Featured products
                    if (state.featuredProducts.isNotEmpty()) {
                        item(key = "featured_spacer") { Spacer(Modifier.height(20.dp)) }
                        item(key = "featured_header") {
                            SectionHeader(
                                title = stringResource(R.string.featured),
                                onSeeAllClick = {
                                    openProductList(ProductListRouteInfo.Featured)
                                },
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                        item(key = "featured_spacer_2") { Spacer(Modifier.height(12.dp)) }
                        item(key = "featured_row") {
                            ProductRow(
                                products = state.featuredProducts,
                                onProductClick = openProductDetails,
                                onWishlistClick = onWishlistClick
                            )
                        }
                    }

                    // Empty state
                    if (state.isEmptyState && !state.isLoading) {
                        item(key = "empty_state") { EmptyHome(onRefresh = { onAction(HomeAction.FetchData) }) }
                    }
                }
            }
        }
    }
    RemoveFavoriteDialog(
        onConfirm = {
            onAction(HomeAction.ToggleFavorite(it))
        }
    )
    state.selectedPromotionAd?.let { ad ->
        CouponOfferScreen(
            ad = ad,
            onClose = { onAction(HomeAction.ClosePromotionAd) },
        )
    }
}
