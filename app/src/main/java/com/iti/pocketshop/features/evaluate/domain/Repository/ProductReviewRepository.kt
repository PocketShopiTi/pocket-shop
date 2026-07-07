package com.iti.pocketshop.features.evaluate.domain.Repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.evaluate.domain.model.ProductReview

interface ProductReviewRepository {

    suspend fun submitReview(
        productId: String,
        customerId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        createdAt: String
    ): PocketResult<ProductReview, PocketDataError>

    suspend fun updateReview(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
    ): PocketResult<Unit, PocketDataError>

    suspend fun deleteReview(
        productId: String,
        reviewId: String,
    ): PocketResult<Unit, PocketDataError>
}
