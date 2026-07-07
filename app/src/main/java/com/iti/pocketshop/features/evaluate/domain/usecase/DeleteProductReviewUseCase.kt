package com.iti.pocketshop.features.evaluate.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.evaluate.domain.Repository.ProductReviewRepository
import javax.inject.Inject

class DeleteProductReviewUseCase @Inject constructor(
    private val repository: ProductReviewRepository,
) {
    suspend operator fun invoke(
        productId: String,
        reviewId: String,
    ): PocketResult<Unit, PocketDataError> {
        return repository.deleteReview(productId = productId, reviewId = reviewId)
    }
}
