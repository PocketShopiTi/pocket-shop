package com.iti.pocketshop.features.productdetails.domain.usecase

import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductDetailsRepository,
) {
    suspend operator fun invoke(productId: String): Result<ProductDetails> =
        repository.getProductDetails(productId)
}
