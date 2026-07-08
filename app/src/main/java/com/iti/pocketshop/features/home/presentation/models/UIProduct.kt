package com.iti.pocketshop.features.home.presentation.models

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.domain.models.Product
import com.iti.pocketshop.features.home.domain.models.Money

@Immutable
data class UIProduct(
    val id: String,
    val title: String,
    val vendor: String,
    val imageUrl: String?,
    val imageAlt: String?,
    val price: Money,
    val compareAtPrice: Money?,
    val discountPercentage: Int?,
    val isFavorite: Boolean,
    val availableForSale: Boolean,
    val handle: String,
    // Store original product for actions if needed
    val originalProduct: Product
)
