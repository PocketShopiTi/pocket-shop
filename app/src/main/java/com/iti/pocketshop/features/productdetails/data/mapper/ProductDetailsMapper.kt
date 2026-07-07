package com.iti.pocketshop.features.productdetails.data.mapper

import androidx.core.graphics.toColorInt
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionType
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionValue
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant
import com.iti.pocketshop.shopify.GetProductByIdQuery
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round
import java.util.Locale

fun GetProductByIdQuery.Product.toDomain(): ProductDetails {
    val visibleOptions = options.filterNot { option ->
        isDefaultOption(
            name = option.name,
            values = option.optionValues.map { it.name },
        )
    }
    val optionLookup = visibleOptions.associateBy { toLowerCase(it.name) }
    val optionIdsByName = optionLookup.mapValues { it.value.id }

    val images = buildList {
        featuredImage?.let { image ->
            add(
                ProductImage(
                    id = "$id:featured",
                    url = image.url,
                    altText = image.altText,
                ),
            )
        }

        this@toDomain.images.edges.forEachIndexed { index, edge ->
            add(
                ProductImage(
                    id = edge.node.url.ifBlank { "$id:image:$index" },
                    url = edge.node.url,
                    altText = edge.node.altText,
                ),
            )
        }

        media.nodes.forEachIndexed { index, node ->
            val previewImage = node.previewImage ?: return@forEachIndexed
            add(
                ProductImage(
                    id = node.id.ifBlank { "$id:media:$index" },
                    url = previewImage.url,
                    altText = previewImage.altText ?: node.alt,
                ),
            )
        }

        variants.edges.forEachIndexed { index, edge ->
            val image = edge.node.image ?: return@forEachIndexed
            add(
                ProductImage(
                    id = "${edge.node.id}:variant:$index",
                    url = image.url,
                    altText = image.altText,
                ),
            )
        }
    }.distinctBy { it.url }

    val reviews = reviewsMetafield
        ?.references
        ?.nodes
        .orEmpty()
        .mapNotNull { it.onMetaobject?.toProductReview() }
    val rating = if (reviews.isEmpty()) {
        0.0
    } else {
        round(reviews.map { it.rating }.average() * 10.0) / 10.0
    }

    val options = visibleOptions.map { option ->
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
                            url = preview.url,
                            altText = preview.altText ?: swatch.image.alt,
                        )
                    },
                )
            },
        )
    }

    val variants = variants.edges.map { edge ->
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
            imageUrl = variant.image?.url,
        )
    }

    return ProductDetails(
        id = id,
        vendor = vendor,
        title = title,
        description = description,
        images = images,
        options = options,
        variants = variants,
        rating = rating,
        reviewCount = reviews.size,
        reviews = reviews,
        isFavorite = false,
    )
}

private fun GetProductByIdQuery.OnMetaobject.toProductReview(): ProductReview? {
    val approvedValue = approved?.value
    if (approvedValue != null && approvedValue.equals("false", ignoreCase = true)) return null

    val ratingValue = rating?.value?.toIntOrNull()?.takeIf { it in 1..5 } ?: return null
    val bodyValue = body?.value.orEmpty().trim()
    val titleValue = title?.value.orEmpty().trim()
    val authorValue = customerName?.value.orEmpty().trim().ifBlank { "Shopper" }

    return ProductReview(
        id = id,
        author = authorValue,
        avatarUrl = null,
        rating = ratingValue,
        date = parseReviewDate(createdAt?.value, updatedAt),
        body = bodyValue,
        title = titleValue,
        customerId = customerId?.value?.takeIf { it.isNotBlank() },
    )
}

internal fun parseReviewDate(createdAt: String?, updatedAt: String): LocalDate {
    val source = createdAt?.takeIf { it.isNotBlank() } ?: updatedAt
    return runCatching {
        Instant.parse(source).toLocalDateTime(TimeZone.UTC).date
    }.getOrElse {
        runCatching {
            LocalDate.parse(source.take(10))
        }.getOrDefault(LocalDate(1970, 1, 1))
    }
}

private fun optionValueId(optionId: String, value: String): String =
    "$optionId:${toLowerCase(value)}"

internal fun isDefaultOption(name: String, values: List<String>): Boolean =
    name.trim()
        .equals("Title", ignoreCase = true) &&
            values.size == 1 &&
            values.single().trim().equals("Default Title", ignoreCase = true)

internal fun classifyOptionType(name: String, hasSwatch: Boolean): ProductOptionType {
    val normalizedName = name.trim().lowercase(Locale.ROOT)
    return when {
        hasSwatch ||
                normalizedName == "color" ||
                normalizedName == "colour" ->
            ProductOptionType.COLOR

        normalizedName == "size" -> ProductOptionType.SIZE
        else -> ProductOptionType.GENERIC
    }
}

internal fun selectedOptionValueIds(
    selectedOptions: List<Pair<String, String>>,
    optionIdsByName: Map<String, String>,
): Map<String, String> = selectedOptions.mapNotNull { (name, value) ->
    optionIdsByName[toLowerCase(name)]?.let { optionId ->
        optionId to optionValueId(optionId, value)
    }
}.toMap()

private fun toLowerCase(value: String): String =
    value.trim().lowercase(Locale.ROOT)

private fun parseColour(value: Any): Long? {
    val text = value.toString().trim()
    if (text.isBlank()) return null
    return runCatching {
        text.toColorInt().toUInt().toLong()
    }.getOrElse {
        colourArgb(text)
    }
}

private val colourTokenArgb: Map<String, Long> = mapOf(
    "black" to 0xFF2C2826,
    "white" to 0xFFFFFFFF,
    "ecru" to 0xFFD8CEC0,
    "cream" to 0xFFD8CEC0,
    "beige" to 0xFFD8CEC0,
    "ivory" to 0xFFD8CEC0,
    "terracotta" to 0xFFB8634A,
    "orange" to 0xFFB8634A,
    "sage" to 0xFF8B9E7E,
    "green" to 0xFF8B9E7E,
    "olive" to 0xFF8B9E7E,
    "red" to 0xFF9E3F3F,
    "burgundy" to 0xFF9E3F3F,
    "maroon" to 0xFF9E3F3F,
    "blue" to 0xFF3F5873,
    "navy" to 0xFF3F5873,
    "indigo" to 0xFF3F5873,
    "pink" to 0xFFD88E9B,
    "rose" to 0xFFD88E9B,
    "grey" to 0xFF8A8378,
    "gray" to 0xFF8A8378,
    "charcoal" to 0xFF8A8378,
    "silver" to 0xFF8A8378,
    "brown" to 0xFF6B4F3F,
    "tan" to 0xFFB49A7D,
    "khaki" to 0xFFB49A7D,
    "yellow" to 0xFFD9B44A,
    "gold" to 0xFFD9B44A,
    "purple" to 0xFF6E5A7E,
)

private const val FALLBACK_COLOUR_ARGB = 0xFFB8B0A5

internal fun colourArgb(value: String): Long {
    val normalized = toLowerCase(value)
    colourTokenArgb[normalized]?.let { return it }
    // Shopify names put the base hue last ("Cloud White", "Core Black", "Solar Red"),
    // so scan tokens from the end to pick the base hue.
    val tokens = normalized.split(NON_LETTER_REGEX).filter { it.isNotBlank() }
    for (token in tokens.asReversed()) {
        colourTokenArgb[token]?.let { return it }
    }
    return FALLBACK_COLOUR_ARGB
}

private val NON_LETTER_REGEX = Regex("[^a-z]+")
