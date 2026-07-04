package com.iti.pocketshop.features.home.data

import com.iti.pocketshop.features.home.domain.models.*
import com.iti.pocketshop.shopify.HomeQuery
import com.iti.pocketshop.shopify.fragment.ProductFields


fun HomeQuery.Data.toDomain(): HomeData {
    return HomeData(

        brands =
            brands.nodes.map {
                it.toDomain()
            },

        categories =
            categories.nodes.map {
                it.toDomain()
            },

        featuredProducts =
            featuredProducts.nodes.map {
                it.productFields.toDomain()
            },

        bestSellers =
            bestSellers.nodes.map {
                it.productFields.toDomain()
            },

        newArrivals =
            newArrivals.nodes.map {
                it.productFields.toDomain()
            },
    )
}

fun ProductFields.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        handle = handle,
        vendor = vendor,
        availableForSale = availableForSale,

        price =
            priceRange.minVariantPrice.toDomain(),

        compareAtPrice =
            compareAtPriceRange.maxVariantPrice
                .takeIf { it.amount > 0.0 }
                ?.toDomain(),

        imageUrl =
            featuredImage?.url,

        imageAlt =
            featuredImage?.altText
    )
}

fun ProductFields.MinVariantPrice.toDomain(): Money {
    return Money(
        amount = amount,
        currencyCode = currencyCode.name
    )
}

fun ProductFields.MaxVariantPrice.toDomain(): Money {
    return Money(
        amount = amount,
        currencyCode = currencyCode.name
    )
}

fun HomeQuery.Node.toDomain(): Brand {
    return Brand(
        id = id,
        brandName = brandName?.value ?: "-",
        handle = handle,
        brandLogoUrl = brandLogo?.value,
    )
}

fun HomeQuery.Node1.toDomain(): Category {
    return Category(
        id = id,
        catName = CategoryName.getTypeByString(catName?.value ?: "-"),
        handle = handle,
        catLogoUrl = catLogo?.value,
    )
}