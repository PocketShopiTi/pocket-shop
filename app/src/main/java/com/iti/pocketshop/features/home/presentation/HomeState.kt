package com.iti.pocketshop.features.home.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.domain.models.Brand
import com.iti.pocketshop.features.home.domain.models.Category
import com.iti.pocketshop.features.home.presentation.models.UIProduct

@Immutable
data class HomeState(
    val isLoading: Boolean = false,
    val isEmptyState: Boolean = true,
    val brands: List<Brand> = emptyList(),
    val categories: List<Category> = emptyList(),
    val featuredProducts: List<UIProduct> = emptyList(),
    val bestSellers: List<UIProduct> = emptyList(),
    val newArrivals: List<UIProduct> = emptyList(),
    val favoriteIds: Set<String> = emptySet()
)
