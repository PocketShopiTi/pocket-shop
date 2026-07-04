package com.iti.pocketshop.features.productdetails.presentation

sealed interface ProductDetailsAction {
    data class ImageSelected(val index: Int) : ProductDetailsAction
    data class OptionSelected(val optionId: String, val valueId: String) : ProductDetailsAction
    data object ToggleFavorite : ProductDetailsAction
    data object ToggleDescription : ProductDetailsAction
    data object DecreaseQuantity : ProductDetailsAction
    data object IncreaseQuantity : ProductDetailsAction
    data object Retry : ProductDetailsAction
    data object BackClicked : ProductDetailsAction
    data object SeeAllReviewsClicked : ProductDetailsAction
    data object AddToCartClicked : ProductDetailsAction
}
