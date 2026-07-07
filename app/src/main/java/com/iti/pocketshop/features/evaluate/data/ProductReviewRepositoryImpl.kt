package com.iti.pocketshop.features.evaluate.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.evaluate.data.datasource.ProductReviewAdminDataSource
import com.iti.pocketshop.features.evaluate.domain.Repository.ProductReviewRepository
import com.iti.pocketshop.features.evaluate.domain.model.ProductReview
import javax.inject.Inject

class ProductReviewRepositoryImpl @Inject constructor(
    private val adminDataSource: ProductReviewAdminDataSource
) : ProductReviewRepository {

    override suspend fun submitReview(
        productId: String,
        customerId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        createdAt: String
    ): PocketResult<ProductReview, PocketDataError> {

         val createResult = adminDataSource.createReviewMetaobject(
            productId = productId,
            customerId = customerId,
            customerName = customerName,
            rating = rating,
            title = title,
            body = body,
            createdAt = createdAt
        )
        val newReviewId = when (createResult) {
            is PocketResult.Success -> createResult.data
            is PocketResult.Error -> return createResult
        }

         val currentIdsResult = adminDataSource.getLinkedReviewIds(productId)
        val currentIds = when (currentIdsResult) {
            is PocketResult.Success -> currentIdsResult.data
            is PocketResult.Error -> return currentIdsResult
        }
        val updatedIds = currentIds + newReviewId
        val setResult = adminDataSource.setLinkedReviewIds(productId, updatedIds)
        return when (setResult) {
            is PocketResult.Success -> PocketResult.Success(
                ProductReview(
                    id = newReviewId,
                    customerId = customerId,
                    customerName = customerName,
                    rating = rating,
                    title = title,
                    body = body,
                    createdAt = createdAt,
                    approved = true
                )
            )
            is PocketResult.Error -> setResult
        }
    }

    override suspend fun updateReview(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
    ): PocketResult<Unit, PocketDataError> {
        return adminDataSource.updateReviewMetaobject(
            reviewId = reviewId,
            customerName = customerName,
            rating = rating,
            title = title,
            body = body,
        )
    }

    override suspend fun deleteReview(
        productId: String,
        reviewId: String,
    ): PocketResult<Unit, PocketDataError> {
        when (val deleteResult = adminDataSource.deleteReviewMetaobject(reviewId)) {
            is PocketResult.Success -> Unit
            is PocketResult.Error -> return deleteResult
        }

        val currentIds = when (val currentIdsResult = adminDataSource.getLinkedReviewIds(productId)) {
            is PocketResult.Success -> currentIdsResult.data
            is PocketResult.Error -> return currentIdsResult
        }
        return adminDataSource.setLinkedReviewIds(
            productId = productId,
            reviewIds = currentIds.filterNot { it == reviewId },
        )
    }
}
