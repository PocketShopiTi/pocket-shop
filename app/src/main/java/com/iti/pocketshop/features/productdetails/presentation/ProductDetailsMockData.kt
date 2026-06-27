package com.iti.pocketshop.features.productdetails.presentation

import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionValue
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant
import kotlinx.datetime.LocalDate

internal object ProductDetailsMockData {
    fun create(productId: String): ProductDetails {
        val colours = listOf(
            ProductOptionValue("ecru", "Ecru", 0xFFD8CEC0),
            ProductOptionValue("black", "Black", 0xFF2C2826),
            ProductOptionValue("terracotta", "Terracotta", 0xFFB8634A),
            ProductOptionValue("sage", "Sage", 0xFF8B9E7E),
        )
        val sizes = listOf("S", "M", "L", "XL").map { size ->
            ProductOptionValue(size.lowercase(), size)
        }
        val price = Money(amount = 329.0, currencyCode = "USD")
        val variants = colours.flatMap { colour ->
            sizes.map { size ->
                ProductVariant(
                    id = "$productId-${colour.id}-${size.id}",
                    selectedOptionValueIds = setOf(colour.id, size.id),
                    price = price,
                    availableForSale = true,
                )
            }
        }

        return ProductDetails(
            id = productId,
            vendor = "Maison Soleil",
            title = "Silk bias-cut midi dress",
            description = "Cut on the bias from pure mulberry silk, this fluid midi dress skims the body with effortless ease. Adjustable straps and a softly draped neckline create an elegant, timeless silhouette.",
            images = listOf(
                ProductImage(
                    id = "view-1",
                    url = "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?auto=format&fit=crop&w=1000&q=85",
                    altText = "Model wearing the silk midi dress",
                ),
                ProductImage(
                    id = "view-2",
                    url = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=1000&q=85",
                    altText = "Detail view of the silk midi dress",
                ),
                ProductImage(
                    id = "view-3",
                    url = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1000&q=85",
                    altText = "Alternate view of the silk midi dress",
                ),
            ),
            options = listOf(
                ProductOption("colour", "Colour", colours),
                ProductOption("size", "Size", sizes),
            ),
            variants = variants,
            rating = 4.3,
            reviewCount = 86,
            reviews = listOf(
                ProductReview(
                    id = "review-1",
                    author = "Amelia Foster",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=160&q=80",
                    rating = 4,
                    date = LocalDate(2026, 6, 12),
                    body = "Absolutely stunning. The silk feels luxurious and the fit is perfect — went true to size.",
                ),
                ProductReview(
                    id = "review-2",
                    author = "James Navarro",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=160&q=80",
                    rating = 4,
                    date = LocalDate(2026, 5, 28),
                    body = "Great quality for the price. Colour is exactly as shown. Delivery was fast too.",
                ),
            ),
            isFavorite = false,
        )
    }
}
