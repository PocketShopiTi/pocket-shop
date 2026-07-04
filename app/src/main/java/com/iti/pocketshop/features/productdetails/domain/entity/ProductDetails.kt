package com.iti.pocketshop.features.productdetails.domain.entity

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct

data class ProductDetails(
    val id: String,
    val vendor: String,
    val title: String,
    val description: String,
    val images: List<ProductImage>,
    val options: List<ProductOption> = emptyList(),
    val variants: List<ProductVariant>,
    val rating: Double,
    val reviewCount: Int,
    val reviews: List<ProductReview>,
    val isFavorite: Boolean,
) {
    val defaultVariant = variants.firstOrNull { it.availableForSale }
        ?: variants.firstOrNull()

    fun isOptionValueAvailable(optionId: String, valueId: String): Boolean =
        variants.any { variant ->
            variant.availableForSale && variant.selectedOptionValueIds[optionId] == valueId
        }

    fun resolveAvailableVariant(
        optionId: String,
        valueId: String,
        currentSelections: Map<String, String>,
    ): ProductVariant? = variants
        .asSequence()
        .filter { variant ->
            variant.availableForSale && variant.selectedOptionValueIds[optionId] == valueId
        }
        .minByOrNull { variant ->
            variant.selectedOptionValueIds.count { (candidateOptionId, candidateValueId) ->
                currentSelections[candidateOptionId] != candidateValueId
            }
        }
}

fun ProductDetails.toFavoriteProduct(): FavoriteProduct {
    return FavoriteProduct(
        id = id,
        title = title,
        imageUrl = images.firstOrNull()?.url.orEmpty(),
    )
}