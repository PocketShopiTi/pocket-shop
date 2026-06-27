package com.iti.pocketshop.features.productdetails.data.repository

import com.iti.pocketshop.features.productdetails.data.datasource.ShopifyRemoteDataSource
import com.iti.pocketshop.features.productdetails.data.mapper.toDomain
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDetailsRepositoryImpl @Inject constructor(
    private val dataSource: ShopifyRemoteDataSource,
) : ProductDetailsRepository {
    companion object {
        private const val TAG = "ProductDetailsRepositor"
    }

    override suspend fun getProductDetails(productId: String): Result<ProductDetails> {
        return runCatching {
            val product = dataSource.getProductById(productId)
            if (product != null) {
                return Result.success(product.toDomain())
            } else {
                return Result.failure(Exception("Can't get Data"))
            }


        }.onFailure {
            return Result.failure(it)
        }
    }
}
