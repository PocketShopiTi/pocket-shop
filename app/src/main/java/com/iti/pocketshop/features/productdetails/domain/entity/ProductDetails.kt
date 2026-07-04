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

    fun imagesFor(variant: ProductVariant?): List<ProductImage> {
        if (variant == null) return images
        val featured = images.firstOrNull() ?: return images
        val colorLabelRegex = selectedColorLabel(variant)
            ?.takeIf { it.isNotBlank() }
            ?.let { label -> Regex("\\b${Regex.escape(label)}\\b", RegexOption.IGNORE_CASE) }
        val matches = images.filter { image ->
            image.url == variant.imageUrl ||
                    (colorLabelRegex != null &&
                            image.altText?.contains(colorLabelRegex) == true)
        }
        if (matches.isEmpty()) return images
        return listOf(featured) + matches.filterNot { it.url == featured.url }
    }

    fun selectedColorLabel(variant: ProductVariant): String? =
        options.firstOrNull { it.type == ProductOptionType.COLOR }?.let { option ->
            val valueId = variant.selectedOptionValueIds[option.id]
            option.values.firstOrNull { it.id == valueId }?.label
        }
}

fun ProductDetails.toFavoriteProduct(): FavoriteProduct {
    return FavoriteProduct(
        id = id,
        title = title,
        imageUrl = images.firstOrNull()?.url.orEmpty(),
    )
}