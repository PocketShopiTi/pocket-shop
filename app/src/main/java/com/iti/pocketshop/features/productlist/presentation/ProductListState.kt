package com.iti.pocketshop.features.productlist.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.presentation.models.UIProduct

@Immutable
data class ProductListState(
    val productListRouteInfo: ProductListRouteInfo = ProductListRouteInfo.None,
    val products: List<UIProduct> = emptyList(),
    val filteredProducts: List<UIProduct> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = false,
    val endCursor: String? = null,
    val favoriteIds: Set<String> = emptySet()
)
