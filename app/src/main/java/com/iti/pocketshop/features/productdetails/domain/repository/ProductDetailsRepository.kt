package com.iti.pocketshop.features.productdetails.domain.repository

import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails

interface ProductDetailsRepository {
    suspend fun getProductDetails(productId: String): Result<ProductDetails>
}
