package com.iti.pocketshop.features.productdetails.presentation

import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOption
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionType
import com.iti.pocketshop.features.productdetails.domain.entity.ProductOptionValue
import com.iti.pocketshop.features.productdetails.domain.entity.ProductVariant

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
        val images = listOf(
            ProductImage(
                id = "view-1",
                url = "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?auto=format&fit=crop&w=1000&q=85",
                altText = "Model wearing the silk midi dress — Ecru",
            ),
            ProductImage(
                id = "view-2",
                url = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=1000&q=85",
                altText = "Detail view of the silk midi dress — Black",
            ),
            ProductImage(
                id = "view-3",
                url = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1000&q=85",
                altText = "Alternate view of the silk midi dress — Terracotta",
            ),
            ProductImage(
                id = "view-4",
                url = "https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=1000&q=85",
                altText = "Back view of the silk midi dress — Sage",
            ),
        )
        val colourImageUrls = mapOf(
            "ecru" to images[0].url,
            "black" to images[1].url,
            "terracotta" to images[2].url,
            "sage" to images[3].url,
        )
        val price = Money(amount = 329.0, currencyCode = "USD")
        val variants = colours.flatMap { colour ->
            sizes.map { size ->
                ProductVariant(
                    id = "$productId-${colour.id}-${size.id}",
                    selectedOptionValueIds = mapOf(
                        "colour" to colour.id,
                        "size" to size.id,
                    ),
                    price = price,
                    compareAtPrice = null,
                    availableForSale = true,
                    imageUrl = colourImageUrls[colour.id],
                )
            }
        }

        return ProductDetails(
            id = productId,
            vendor = "Maison Soleil",
            title = "Silk bias-cut midi dress",
            description = "Cut on the bias from pure mulberry silk, this fluid midi dress skims the body with effortless ease. Adjustable straps and a softly draped neckline create an elegant, timeless silhouette.",
            images = images,
            options = listOf(
                ProductOption("colour", "Colour", ProductOptionType.COLOR, colours),
                ProductOption("size", "Size", ProductOptionType.SIZE, sizes),
            ),
            variants = variants,
            isFavorite = false,
        )
    }
}
