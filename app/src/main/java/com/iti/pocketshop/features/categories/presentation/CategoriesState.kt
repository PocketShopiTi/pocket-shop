package com.iti.pocketshop.features.categories.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.categories.domain.models.CategoryItem

@Immutable
data class CategoriesState(
    val isLoading: Boolean = false,
    val categories: List<CategoryItem> = emptyList(),
    val filteredCategories: List<CategoryItem> = emptyList(),
    val searchQuery: String = "",
)
