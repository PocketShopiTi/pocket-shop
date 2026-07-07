package com.iti.pocketshop.features.evaluate.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.evaluate.domain.Repository.ProductReviewRepository
import javax.inject.Inject

class UpdateProductReviewUseCase @Inject constructor(
    private val repository: ProductReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
    ): PocketResult<Unit, PocketDataError> {
        if (customerName.isBlank()) {
            return PocketResult.Error(PocketDataError.Validation.EMPTY_CUSTOMER_NAME)
        }
        if (title.isBlank()) {
            return PocketResult.Error(PocketDataError.Validation.EMPTY_TITLE)
        }
        if (body.isBlank()) {
            return PocketResult.Error(PocketDataError.Validation.EMPTY_BODY)
        }
        if (rating !in 1..5) {
            return PocketResult.Error(PocketDataError.Validation.INVALID_RATING)
        }

        return repository.updateReview(
            reviewId = reviewId,
            customerName = customerName.trim(),
            rating = rating,
            title = title.trim(),
            body = body.trim(),
        )
    }
}
