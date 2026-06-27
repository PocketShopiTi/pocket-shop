package com.iti.pocketshop.features.productdetails.data.mapper

import androidx.core.text.HtmlCompat
import com.iti.pocketshop.features.productdetails.data.model.OptionDto
import com.iti.pocketshop.features.productdetails.data.model.ProductDto
import com.iti.pocketshop.features.productdetails.data.model.VariantDto
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionValue
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant
import java.util.Locale

private const val TEMPORARY_CURRENCY_CODE = "USD"

fun ProductDto.toDomain(): ProductDetails {
    val sortedOptions = options.sortedBy(OptionDto::position)

    return ProductDetails(
        id = adminGraphqlApiId,
        vendor = vendor,
        title = title,
        description = "",
        images = images.sortedBy { it.position }.map { image ->
            ProductImage(
                id = image.id.toString(),
                url = image.src,
                altText = image.alt,
            )
        },
        options = sortedOptions.map(OptionDto::toDomain),
        variants = variants.map { it.toDomain(sortedOptions) },
        rating = 0.0,
        reviewCount = 0,
        reviews = emptyList(),
        isFavorite = false,
    )
}

private fun OptionDto.toDomain(): ProductOption {
    val isColour = name.equals("color", ignoreCase = true) ||
        name.equals("colour", ignoreCase = true)
    return ProductOption(
        id = id.toString(),
        name = name,
        values = values.map { value ->
            ProductOptionValue(
                id = optionValueId(id, value),
                label = value,
                swatchArgb = if (isColour) colourArgb(value) else null,
            )
        },
    )
}

private fun VariantDto.toDomain(options: List<OptionDto>): ProductVariant {
    val variantOptions = listOf(option1, option2, option3)
    return ProductVariant(
        id = adminGraphqlApiId,
        selectedOptionValueIds = options.mapIndexedNotNull { index, option ->
            variantOptions.getOrNull(index)?.let { value -> optionValueId(option.id, value) }
        }.toSet(),
        price = Money(
            amount = price.toDoubleOrNull() ?: 0.0,
            currencyCode = TEMPORARY_CURRENCY_CODE,
        ),
        availableForSale = inventoryQuantity > 0 ||
            inventoryPolicy.equals("continue", ignoreCase = true),
    )
}

private fun optionValueId(optionId: Long, value: String): String =
    "$optionId:${value.trim().lowercase(Locale.ROOT)}"

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
