package com.iti.pocketshop.features.home.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.domain.models.Category
import com.iti.pocketshop.features.home.domain.models.Product

@Immutable
data class HomeState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val bestSellers: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList(),
    val favoriteIds: Set<String> = emptySet()
)