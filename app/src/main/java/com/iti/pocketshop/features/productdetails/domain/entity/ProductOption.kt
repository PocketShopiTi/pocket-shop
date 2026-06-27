package com.iti.pocketshop.features.productdetails.domain.entity

data class ProductOption(
    val id: String,
    val name: String,
    val values: List<ProductOptionValue>,
)

data class ProductOptionValue(
    val id: String,
    val label: String,
    val swatchArgb: Long? = null,
)
