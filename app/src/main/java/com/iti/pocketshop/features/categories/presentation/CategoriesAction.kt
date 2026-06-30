package com.iti.pocketshop.features.categories.presentation

sealed interface CategoriesAction {
    data object FetchCategories : CategoriesAction
    data class SearchCategories(val query: String) : CategoriesAction
}
