package com.iti.pocketshop.features.productdetails.data.mapper

import androidx.core.graphics.toColorInt
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionType
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionValue
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant
import com.iti.pocketshop.shopify.GetProductByIdQuery
import java.util.Locale

fun GetProductByIdQuery.Product.toDomain(): ProductDetails {
    val visibleOptions = options.filterNot { option ->
        isSyntheticDefaultOption(
            name = option.name,
            values = option.optionValues.map { it.name },
        )
    }
    val optionLookup = visibleOptions.associateBy { it.name.lowercase(Locale.ROOT) }
    val optionIdsByName = optionLookup.mapValues { it.value.id }

    val images = buildList {
        featuredImage?.let { image ->
            add(
                ProductImage(
                    id = "$id:featured",
                    url = image.url.toString(),
                    altText = image.altText,
                ),
            )
        }

        this@toDomain.images.edges.forEachIndexed { index, edge ->
            add(
                ProductImage(
                    id = edge.node.url.toString().ifBlank { "$id:image:$index" },
                    url = edge.node.url.toString(),
                    altText = edge.node.altText,
                ),
            )
        }

        media.nodes.forEachIndexed { index, node ->
            val previewImage = node.previewImage ?: return@forEachIndexed
            add(
                ProductImage(
                    id = node.id.ifBlank { "$id:media:$index" },
                    url = previewImage.url.toString(),
                    altText = previewImage.altText ?: node.alt,
                ),
            )
        }
    }.distinctBy { it.url }

    return ProductDetails(
        id = id,
        vendor = vendor,
        title = title,
        description = description,
        images = images,
        options = visibleOptions.map { option ->
            val optionName = option.name
            val optionType = classifyOptionType(
                name = optionName,
                hasSwatch = option.optionValues.any { it.swatch != null },
            )
            ProductOption(
                id = option.id,
                name = optionName,
                type = optionType,
                values = option.optionValues.map { value ->
                    val swatch = value.swatch
                    ProductOptionValue(
                        id = optionValueId(option.id, value.name),
                        label = value.name,
                        swatchArgb = when {
                            swatch?.color != null -> parseColour(swatch.color)
                            optionType == ProductOptionType.COLOR -> colourArgb(value.name)
                            else -> null
                        },
                        swatchImage = swatch?.image?.previewImage?.let { preview ->
                            ProductImage(
                                id = "${option.id}:${value.name}:swatch",
                                url = preview.url.toString(),
                                altText = preview.altText ?: swatch.image.alt,
                            )
                        },
                    )
                },
            )
        },
        variants = variants.edges.map { edge ->
            val variant = edge.node
            ProductVariant(
                id = variant.id,
                selectedOptionValueIds = selectedOptionValueIds(
                    selectedOptions = variant.selectedOptions.map { it.name to it.value },
                    optionIdsByName = optionIdsByName,
                ),
                price = Money(
                    amount = variant.price.amount.toString().toDoubleOrNull() ?: 0.0,
                    currencyCode = variant.price.currencyCode.name,
                ),
                compareAtPrice = variant.compareAtPrice?.let {
                    Money(
                        amount = it.amount.toString().toDoubleOrNull() ?: 0.0,
                        currencyCode = it.currencyCode.name,
                    )
                },
                availableForSale = variant.availableForSale,
            )
        },
        rating = 0.0,
        reviewCount = 0,
        reviews = emptyList(),
        isFavorite = false,
    )
}

private fun optionValueId(optionId: String, value: String): String =
    "$optionId:${value.trim().lowercase(Locale.ROOT)}"

internal fun isSyntheticDefaultOption(name: String, values: List<String>): Boolean =
    name.trim().equals("Title", ignoreCase = true) &&
        values.size == 1 &&
        values.single().trim().equals("Default Title", ignoreCase = true)

internal fun classifyOptionType(name: String, hasSwatch: Boolean): ProductOptionType {
    val normalizedName = name.trim().lowercase(Locale.ROOT)
    return when {
        hasSwatch || normalizedName == "color" || normalizedName == "colour" ->
            ProductOptionType.COLOR
        normalizedName == "size" -> ProductOptionType.SIZE
        else -> ProductOptionType.GENERIC
    }
}

internal fun selectedOptionValueIds(
    selectedOptions: List<Pair<String, String>>,
    optionIdsByName: Map<String, String>,
): Set<String> = selectedOptions.mapNotNull { (name, value) ->
    optionIdsByName[name.lowercase(Locale.ROOT)]?.let { optionId ->
        optionValueId(optionId, value)
    }
}.toSet()

private fun parseColour(value: Any): Long? {
    val text = value.toString().trim()
    if (text.isBlank()) return null
    return runCatching {
        text.toColorInt().toUInt().toLong()
    }.getOrElse {
        colourArgb(text)
    }
}

private fun colourArgb(value: String): Long = when (value.trim().lowercase(Locale.ROOT)) {
    "black" -> 0xFF2C2826
    "white" -> 0xFFFFFFFF
    "ecru", "cream", "beige" -> 0xFFD8CEC0
    "terracotta", "orange" -> 0xFFB8634A
    "sage", "green" -> 0xFF8B9E7E
    "red", "burgundy" -> 0xFF9E3F3F
    "blue", "navy" -> 0xFF3F5873
    "pink" -> 0xFFD88E9B
    "grey", "gray" -> 0xFF8A8378
    else -> 0xFFB8B0A5
}
