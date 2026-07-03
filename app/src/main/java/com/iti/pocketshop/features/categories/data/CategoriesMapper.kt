package com.iti.pocketshop.features.categories.data

import com.iti.pocketshop.features.categories.domain.models.CategoryItem
import com.iti.pocketshop.shopify.GetCategoriesQuery

fun GetCategoriesQuery.Node.toDomain(): CategoryItem {
    return CategoryItem(
        id = id,
        title = title,
        handle = handle,
        itemCount = 0, // Not available in Storefront API
        imageUrl = image?.url,
        imageAlt = image?.altText
    )
}
