package com.iti.pocketshop.features.productdetails.domain.repository

import com.iti.pocketshop.features.productdetails.domain.entity.AiComparisonResult

interface AiCompareRepository {
    suspend fun compareProducts(
        currentProductTitle: String,
        currentProductDesc: String,
        currentProductVendor: String,
        currentProductPrice: String,
        currentProductRating: Double,
        selectedProductsInfo: Map<String, String>
    ): Result<AiComparisonResult>
}
