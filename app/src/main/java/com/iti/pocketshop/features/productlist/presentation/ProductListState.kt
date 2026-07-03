package com.iti.pocketshop.features.productlist.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.domain.models.Product

@Immutable
data class ProductListState(
    val productListRouteInfo: ProductListRouteInfo = ProductListRouteInfo.None,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = false,
    val endCursor: String? = null,
)
