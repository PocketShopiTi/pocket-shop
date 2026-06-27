package com.iti.pocketshop.features.productdetails.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    @SerialName("product")
    val product: ProductDto
)