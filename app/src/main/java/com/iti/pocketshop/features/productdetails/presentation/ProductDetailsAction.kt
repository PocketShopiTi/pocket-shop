package com.iti.pocketshop.features.productdetails.presentation

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview

sealed interface ProductDetailsAction {
    data class ProductChanged(val productId: String) : ProductDetailsAction
    data class ImageSelected(val index: Int) : ProductDetailsAction
    data class OptionSelected(val optionId: String, val valueId: String) : ProductDetailsAction
    data class ToggleFavorite(val product: FavoriteProduct) : ProductDetailsAction
    data object ToggleDescription : ProductDetailsAction
    data object DecreaseQuantity : ProductDetailsAction
    data object IncreaseQuantity : ProductDetailsAction
    data object Retry : ProductDetailsAction
    data object BackClicked : ProductDetailsAction
    data object SeeAllReviewsClicked : ProductDetailsAction
    data object HideAllReviewsClicked : ProductDetailsAction
    data object AddToCartClicked : ProductDetailsAction
    data object CartFeedbackFinished : ProductDetailsAction
    data class WriteReviewClicked(val defaultCustomerName: String) : ProductDetailsAction
    data class EditReviewClicked(val review: ProductReview) : ProductDetailsAction
    data object ReviewEditorDismissed : ProductDetailsAction
    data class ReviewSubmitted(
        val customerId: String,
        val customerName: String,
        val rating: Int,
        val title: String,
        val body: String,
    ) : ProductDetailsAction
    data class DeleteReviewClicked(val review: ProductReview) : ProductDetailsAction
    data object DeleteReviewDismissed : ProductDetailsAction
    data object DeleteReviewConfirmed : ProductDetailsAction
}
