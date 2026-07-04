package com.iti.pocketshop.features.brands.data

import com.iti.pocketshop.features.brands.domain.models.BrandItem
import com.iti.pocketshop.shopify.GetBrandsQuery

fun GetBrandsQuery.Node.toDomain(): BrandItem {
    return BrandItem(
        id = id,
        title = brandName?.value ?: "-",
        handle = handle,
        imageUrl = brandLogo?.value,
    )
}
