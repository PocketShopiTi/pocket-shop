package com.iti.pocketshop.features.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.usecase.GetLocalFavoritesUseCase
import com.iti.pocketshop.common.favorites.domain.usecase.ToggleFavoriteUseCase
import com.iti.pocketshop.core.components.ErrorDialogController
import com.iti.pocketshop.core.networkutils.onError
import com.iti.pocketshop.core.networkutils.onSuccess
import com.iti.pocketshop.features.home.presentation.models.toUIProduct
import com.iti.pocketshop.features.productlist.domain.GetProductListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getLocalFavoritesUseCase: GetLocalFavoritesUseCase,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProductListState())
    val state = _state
        .combine(getLocalFavoritesUseCase()) { state, favorites ->
            val favoriteIds = favorites.map(FavoriteProduct::id).toSet()
            state.copy(
                favoriteIds = favoriteIds,
                products = state.products.map { it.copy(isFavorite = favoriteIds.contains(it.id)) },
                filteredProducts = state.filteredProducts.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
            )
        }
        .onStart {
            if (!hasLoadedInitialData) {
                loadProducts(isRefresh = true)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProductListState()
        )

    fun onAction(action: ProductListAction) {
        when (action) {
            is ProductListAction.UpdateRouteInfo -> updateRouteInfo(action.routeInfo)
            ProductListAction.LoadMore -> loadProducts(isRefresh = false)
            ProductListAction.Refresh -> loadProducts(isRefresh = true)
            is ProductListAction.SearchProducts -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        filteredProducts = if (action.query.isBlank()) {
                            it.products
                        } else {
                            it.products.filter { product ->
                                product.title.contains(action.query, ignoreCase = true)
                            }
                        }
                    )
                }
            }
            is ProductListAction.ToggleFavorite -> toggleFavorite(action.product)
        }
    }

    private fun toggleFavorite(product: FavoriteProduct) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product)
                .onError {
                    ErrorDialogController.sendEvent(it)
                }
        }
    }

    private fun updateRouteInfo(info: ProductListRouteInfo) {
        _state.update {
            it.copy(
                productListRouteInfo = info
            )
        }
    }

    private fun loadProducts(isRefresh: Boolean) {
        val currentState = _state.value

        // Don't load more if already loading or no more pages
        if (!isRefresh && (currentState.isLoadingMore || !currentState.hasNextPage)) return
        if (isRefresh && currentState.isLoading) return

        viewModelScope.launch {
            _state.update {
                if (isRefresh) it.copy(isLoading = true)
                else it.copy(isLoadingMore = true)
            }

            val currentStateRouteInfo = _state.value.productListRouteInfo

            getProductListUseCase(
                first = 20,
                after = if (isRefresh) null else currentState.endCursor,
                sortKey = currentStateRouteInfo.sortKey(),
                reverse = currentStateRouteInfo.isReverse(),
                query =  currentStateRouteInfo.query()
            )
                .onSuccess { page ->
                    _state.update { current ->
                        val newProducts =
                            if (isRefresh) page.products.map { it.toUIProduct(current.favoriteIds.contains(it.id)) }
                            else current.products + page.products.map { it.toUIProduct(current.favoriteIds.contains(it.id)) }
                        
                        current.copy(
                            products = newProducts,
                            filteredProducts = if (current.searchQuery.isEmpty()) newProducts else newProducts.filter { product ->
                                product.title.contains(current.searchQuery, ignoreCase = true)
                            },
                            hasNextPage = page.hasNextPage,
                            endCursor = page.endCursor,
                            isLoading = false,
                            isLoadingMore = false,
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(isLoading = false, isLoadingMore = false)
                    }
                    ErrorDialogController.sendEvent(error)
                }
        }
    }
}
