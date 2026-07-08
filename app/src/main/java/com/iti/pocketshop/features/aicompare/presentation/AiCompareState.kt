package com.iti.pocketshop.features.aicompare.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.features.home.domain.models.Product
import com.iti.pocketshop.features.productdetails.domain.entity.AiComparisonResult
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails

data class AiCompareState(
    val productId: String = "",
    val product: ProductDetails? = null,
    val isSearchingSimilar: Boolean = false,
    val similarProducts: List<Product> = emptyList(),
    val selectedProductsToCompare: List<Product> = emptyList(),
    val isComparingWithAi: Boolean = false,
    val aiComparisonResult: AiComparisonResult? = null,
    val isLoading: Boolean = true,
    val error: PocketDataError? = null,
)
