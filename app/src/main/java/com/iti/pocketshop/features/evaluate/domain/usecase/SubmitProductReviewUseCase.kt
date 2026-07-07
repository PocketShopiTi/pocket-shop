package com.iti.pocketshop.features.evaluate.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.evaluate.domain.Repository.ProductReviewRepository
import com.iti.pocketshop.features.evaluate.domain.model.ProductReview
import javax.inject.Inject
import kotlin.time.Clock


class SubmitProductReviewUseCase @Inject constructor(
    private val repository: ProductReviewRepository
) {
    suspend operator fun invoke(
        productId: String,
        customerId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String
    ): PocketResult<ProductReview, PocketDataError> {

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

        val createdAt = Clock.System.now().toString()

        return repository.submitReview(
            productId = productId,
            customerId = customerId,
            customerName = customerName.trim(),
            rating = rating,
            title = title.trim(),
            body = body.trim(),
            createdAt = createdAt
        )
    }
}
