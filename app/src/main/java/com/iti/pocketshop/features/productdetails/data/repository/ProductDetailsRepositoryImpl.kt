package com.iti.pocketshop.features.productdetails.data.repository

import android.util.Log
import com.iti.pocketshop.BuildConfig
import com.iti.pocketshop.features.productdetails.data.mapper.toDomain
import com.iti.pocketshop.features.productdetails.data.service.retrofit.ProductsService
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

@Singleton
class ProductDetailsRepositoryImpl @Inject constructor(
    private val productsService: ProductsService,
) : ProductDetailsRepository {
    companion object{
        private const val TAG = "ProductDetailsRepositor"
    }

    override suspend fun getProductDetails(productId: String): Result<ProductDetails> {
        Log.d("TOKEN_TEST", BuildConfig.ADMIN_TOKEN)
        val restProductId = productId.substringAfterLast('/')
        return Result.success(productsService.getProductByID(restProductId).product.toDomain())
    }
}
