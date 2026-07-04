package com.iti.pocketshop.features.productdetails.data.datasource

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.shopify.GetProductByIdQuery

interface ProductDetailsDataSource {
    suspend fun getProductById(
        productId: String,
    ): PocketResult<GetProductByIdQuery.Product, PocketDataError.Remote>
}