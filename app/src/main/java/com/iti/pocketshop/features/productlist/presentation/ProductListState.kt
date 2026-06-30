package com.iti.pocketshop.features.productlist.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.domain.models.Product
import com.iti.pocketshop.features.productlist.domain.ProductListType

@Immutable
data class ProductListState(
    val listType: ProductListType = ProductListType.FEATURED,
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = false,
    val endCursor: String? = null,
)
