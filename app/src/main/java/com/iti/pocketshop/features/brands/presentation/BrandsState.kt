package com.iti.pocketshop.features.brands.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.brands.domain.models.BrandItem

@Immutable
data class BrandsState(
    val isLoading: Boolean = false,
    val brands: List<BrandItem> = emptyList(),
    val filteredBrands: List<BrandItem> = emptyList(),
    val searchQuery: String = "",
)
