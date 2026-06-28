package com.iti.pocketshop.features.productdetails.data.repository

import com.iti.pocketshop.features.productdetails.data.datasource.RemoteDataSource
import com.iti.pocketshop.features.productdetails.data.mapper.toDomain
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDetailsRepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource,
) : ProductDetailsRepository {
    override suspend fun getProductDetails(productId: String): Result<ProductDetails> {
        return try {
            val product = dataSource.getProductById(productId).toDomain()
            Result.success(product)
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }
}
