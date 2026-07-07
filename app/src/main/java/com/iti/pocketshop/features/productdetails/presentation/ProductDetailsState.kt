package com.iti.pocketshop.features.productdetails.presentation

import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview

data class ProductDetailsState(
    val productId: String = "",
    val product: ProductDetails? = null,
    val cartId: String? = null,
    val selectedImageIndex: Int = 0,
    val selectedOptionValueIds: Map<String, String> = emptyMap(),
    val quantity: Int = 1,
    val isDescriptionExpanded: Boolean = false,
    val isFavorite: Boolean = false,
    val isAddedToCart: Boolean = false,
    val isReviewEditorVisible: Boolean = false,
    val editingReview: ProductReview? = null,
    val reviewCustomerName: String = "",
    val reviewActionInProgress: Boolean = false,
    val reviewToDelete: ProductReview? = null,
    val isShowingAllReviews: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val selectedVariant: ProductVariant?
        get() {
            val selectedValues = selectedOptionValueIds.values.toSet()
            return product?.variants?.firstOrNull { variant ->
                variant.selectedOptionValueIds == selectedValues
            }
        }

    val totalPrice: Money?
        get() = selectedVariant?.price?.let { price ->
            price.copy(amount = price.amount * quantity)
        }

    fun isOptionValueAvailable(optionId: String, valueId: String): Boolean {
        val product = product ?: return false
        val candidateValues = selectedOptionValueIds.toMutableMap().apply {
            put(optionId, valueId)
        }.values.toSet()
        return product.variants.any { variant ->
            variant.availableForSale && candidateValues.all(variant.selectedOptionValueIds::contains)
        }
    }
}
