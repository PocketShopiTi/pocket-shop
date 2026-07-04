package com.iti.pocketshop.features.productdetails.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant

data class ProductDetailsState(
    val productId: String = "",
    val product: ProductDetails? = null,
    val selectedImageIndex: Int = 0,
    val selectedOptionValueIds: Map<String, String> = emptyMap(),
    val quantity: Int = 1,
    val isDescriptionExpanded: Boolean = false,
    val isFavorite: Boolean = false,
    val isAddedToCart: Boolean = false,
    val isLoading: Boolean = true,
    val error: PocketDataError? = null,
) {
    val selectedVariant: ProductVariant?
        get() = product?.variants?.firstOrNull { variant ->
            variant.selectedOptionValueIds == selectedOptionValueIds
        }

    val galleryImages: List<ProductImage>
        get() = product?.imagesFor(selectedVariant).orEmpty()

    val totalPrice: Money?
        get() = selectedVariant?.price?.let { price ->
            price.copy(amount = price.amount * quantity)
        }

    fun isOptionValueAvailable(optionId: String, valueId: String): Boolean {
        return product?.isOptionValueAvailable(optionId, valueId) == true
    }
}
