package com.iti.pocketshop.features.productdetails.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.iti.pocketshop.features.aicompare.presentation.ProductDetailsState
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.ui.theme.PocketShopTheme

@Preview(name = "No options", showBackground = true, widthDp = 390, heightDp = 1180)
@Composable
private fun ProductDetailsNoOptionsPreview() {
    val original = ProductDetailsMockData.create("no-options")
    ProductDetailsPreview(
        product = original.copy(
            options = emptyList(),
            variants = original.variants.map { it.copy(selectedOptionValueIds = emptyMap()) },
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

@Preview(
    name = "Dark theme",
    showBackground = true,
    widthDp = 390,
    heightDp = 1180,
)
@Composable
private fun ProductDetailsDarkPreview() {
    ProductDetailsPreview(
        product = ProductDetailsMockData.create("dark-theme"),
        isDarkTheme = true,
    )
}

@Preview(
    name = "RTL",
    showBackground = true,
    widthDp = 390,
    heightDp = 1180,
    locale = "ar",
)
@Composable
private fun ProductDetailsRtlPreview() {
    ProductDetailsPreview(ProductDetailsMockData.create("rtl"))
}

@Composable
private fun ProductDetailsPreview(
    product: ProductDetails,
    isDarkTheme: Boolean = false,
) {
    val selectedOptions = product.options.mapNotNull { option ->
        option.values.firstOrNull()?.let { option.id to it.id }
    }.toMap()
    PocketShopTheme(isDarkTheme = isDarkTheme) {
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
