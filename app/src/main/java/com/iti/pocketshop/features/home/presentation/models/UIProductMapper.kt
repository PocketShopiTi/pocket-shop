package com.iti.pocketshop.features.home.presentation.models

import com.iti.pocketshop.features.home.domain.models.Product

fun Product.toUIProduct(
    isFavorite: Boolean
): UIProduct {
    val compareAtAmount = compareAtPrice?.amount ?: 0.0
    val hasDiscount = compareAtAmount > price.amount
    val discountPercentage = if (hasDiscount) {
        ((1 - price.amount / compareAtAmount) * 100).toInt()
    } else null

    return UIProduct(
        id = id,
        title = title,
        vendor = vendor,
        imageUrl = imageUrl,
        imageAlt = imageAlt,
        price = price,
        compareAtPrice = compareAtPrice,
        discountPercentage = discountPercentage,
        isFavorite = isFavorite,
        availableForSale = availableForSale,
        handle = handle,
        originalProduct = this
    )
}
