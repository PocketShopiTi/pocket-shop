package com.iti.pocketshop.features.productdetails.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.ui.theme.PocketShopTheme

@Preview(name = "No options", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsNoOptionsPreview() {
    val original = ProductDetailsMockData.create("no-options")
    ProductDetailsPreview(
        product = original.copy(
            options = emptyList(),
            variants = original.variants.map { it.copy(selectedOptionValueIds = emptySet()) },
        ),
    )
}

@Preview(name = "Short description", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsShortDescriptionPreview() {
    ProductDetailsPreview(
        ProductDetailsMockData.create("short-description")
            .copy(description = "A lightweight everyday essential."),
    )
}

@Preview(name = "Empty description", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsEmptyDescriptionPreview() {
    ProductDetailsPreview(
        ProductDetailsMockData.create("empty-description").copy(description = ""),
    )
}

@Preview(name = "No reviews", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsNoReviewsPreview() {
    ProductDetailsPreview(
        ProductDetailsMockData.create("no-reviews").copy(
            rating = 0.0,
            reviewCount = 0,
            reviews = emptyList(),
        ),
    )
}

@Preview(name = "Three reviews", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsThreeReviewsPreview() {
    val product = ProductDetailsMockData.create("three-reviews")
    ProductDetailsPreview(
        product.copy(
            reviewCount = 3,
            reviews = product.reviews + product.reviews.first().copy(id = "review-3"),
        ),
    )
}

@Preview(name = "More reviews", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsMoreReviewsPreview() {
    val product = ProductDetailsMockData.create("more-reviews")
    ProductDetailsPreview(
        product.copy(
            reviewCount = 4,
            reviews = product.reviews + listOf(
                product.reviews.first().copy(id = "review-3"),
                product.reviews.last().copy(id = "review-4"),
            ),
        ),
    )
}

@Composable
private fun ProductDetailsPreview(product: ProductDetails) {
    val selectedOptions = product.options.mapNotNull { option ->
        option.values.firstOrNull()?.let { option.id to it.id }
    }.toMap()
    PocketShopTheme(isDarkTheme = false) {
        ProductDetailsScreen(
            state = ProductDetailsState(
                productId = product.id,
                product = product,
                selectedOptionValueIds = selectedOptions,
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
