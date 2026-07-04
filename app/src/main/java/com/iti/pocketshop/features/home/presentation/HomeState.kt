package com.iti.pocketshop.features.home.presentation

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.features.home.domain.models.Category
import com.iti.pocketshop.features.home.domain.models.Product
import com.iti.pocketshop.features.home.domain.models.PromotionAd

@Immutable
data class HomeState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val bestSellers: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val promotionAds: List<PromotionAd> = emptyList(),
    val selectedPromotionAd: PromotionAd? = null,
)
