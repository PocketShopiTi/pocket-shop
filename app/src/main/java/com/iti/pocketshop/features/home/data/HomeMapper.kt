package com.iti.pocketshop.features.home.data

import com.google.firebase.firestore.DocumentSnapshot
import com.iti.pocketshop.features.home.domain.models.*
import com.iti.pocketshop.shopify.HomeQuery
import com.iti.pocketshop.shopify.fragment.ProductFields


fun HomeQuery.Data.toDomain(): HomeData {
    return HomeData(

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

fun HomeQuery.Node.toDomain(): Category {
    return Category(
        id = id,
        title = title,
        handle = handle,
        imageUrl = image?.url,
        imageAlt = image?.altText
    )
}

fun DocumentSnapshot.toPromotionAdOrNull(): PromotionAd? {
    val title = getString(TITLE_FIELD)?.trim().orEmpty()
    val couponCode = getString(COUPON_CODE_FIELD)?.trim().orEmpty()

    if (title.isBlank() || couponCode.isBlank()) return null

    return PromotionAd(
        id = id,
        title = title,
        description = getString(DESCRIPTION_FIELD)?.trim().orEmpty(),
        imageUrl = getString(IMAGE_URL_FIELD)?.trim()?.takeIf(String::isNotBlank),
        couponCode = couponCode,
        buttonText = getString(BUTTON_TEXT_FIELD)?.trim().orEmpty(),
    )
}

private const val TITLE_FIELD = "title"
private const val DESCRIPTION_FIELD = "description"
private const val IMAGE_URL_FIELD = "imageUrl"
private const val COUPON_CODE_FIELD = "couponCode"
private const val BUTTON_TEXT_FIELD = "buttonText"
