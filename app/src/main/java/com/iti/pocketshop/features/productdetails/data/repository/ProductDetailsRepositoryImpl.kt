package com.iti.pocketshop.features.productdetails.data.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.productdetails.data.datasource.ProductDetailsDataSource
import com.iti.pocketshop.features.productdetails.data.mapper.toDomain
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDetailsRepositoryImpl @Inject constructor(
    private val dataSource: ProductDetailsDataSource,
) : ProductDetailsRepository {
    override suspend fun getProductDetails(
        productId: String,
    ): PocketResult<ProductDetails, PocketDataError.Remote> =
        dataSource.getProductById(productId)
            .map { product -> product.toDomain() }
}
