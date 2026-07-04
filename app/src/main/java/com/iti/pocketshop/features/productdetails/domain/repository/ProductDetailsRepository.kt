package com.iti.pocketshop.features.productdetails.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails

interface ProductDetailsRepository {
    suspend fun getProductDetails(
        productId: String,
    ): PocketResult<ProductDetails, PocketDataError.Remote>
}
