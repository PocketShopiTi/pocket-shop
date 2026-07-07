package com.iti.pocketshop.features.evaluate.data.datasource


import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface ProductReviewAdminDataSource {

    suspend fun createReviewMetaobject(
        productId: String,
        customerId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        createdAt: String
    ): PocketResult<String, PocketDataError>

    suspend fun getLinkedReviewIds(
        productId: String
    ): PocketResult<List<String>, PocketDataError>

    suspend fun setLinkedReviewIds(
        productId: String,
        reviewIds: List<String>
    ): PocketResult<Unit, PocketDataError>

    suspend fun updateReviewMetaobject(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
    ): PocketResult<Unit, PocketDataError>

    suspend fun deleteReviewMetaobject(
        reviewId: String,
    ): PocketResult<Unit, PocketDataError>
}
