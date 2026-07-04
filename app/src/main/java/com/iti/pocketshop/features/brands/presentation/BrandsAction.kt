package com.iti.pocketshop.features.brands.presentation

sealed interface BrandsAction {
    data object FetchBrands : BrandsAction
    data class SearchBrands(val query: String) : BrandsAction
}
