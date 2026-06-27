package com.iti.pocketshop.features.productdetails.domain.entity

data class ProductDetails(
    val id: String,
    val vendor: String,
    val title: String,
    val description: String,
    val images: List<ProductImage>,
    val options: List<ProductOption>,
    val variants: List<ProductVariant>,
    val rating: Double,
    val reviewCount: Int,
    val reviews: List<ProductReview>,
    val isFavorite: Boolean,
)
