package com.iti.pocketshop.features.brands.data

import com.iti.pocketshop.features.brands.domain.models.BrandItem
import com.iti.pocketshop.shopify.GetBrandsQuery

fun GetBrandsQuery.Node.toDomain(): BrandItem {
    return BrandItem(
        id = id,
        title = title,
        handle = handle,
        itemCount = 0, // Not available in Storefront API
        imageUrl = image?.url,
        imageAlt = image?.altText
    )
}
