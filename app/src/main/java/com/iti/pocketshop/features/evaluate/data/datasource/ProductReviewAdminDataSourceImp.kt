package com.iti.pocketshop.features.evaluate.data.datasource

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.di.AdminApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.shopify.admin.CreateProductReviewMutation
import com.iti.pocketshop.shopify.admin.DeleteProductReviewMutation
import com.iti.pocketshop.shopify.admin.GetProductReviewIdsQuery
import com.iti.pocketshop.shopify.admin.SetProductReviewsMutation
import com.iti.pocketshop.shopify.admin.UpdateProductReviewMutation
import com.iti.pocketshop.shopify.admin.type.MetaobjectCapabilityDataInput
import com.iti.pocketshop.shopify.admin.type.MetaobjectCapabilityDataPublishableInput
import com.iti.pocketshop.shopify.admin.type.MetaobjectCreateInput
import com.iti.pocketshop.shopify.admin.type.MetaobjectFieldInput
import com.iti.pocketshop.shopify.admin.type.MetaobjectStatus
import com.iti.pocketshop.shopify.admin.type.MetaobjectUpdateInput
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ProductReviewAdminDataSourceImpl @Inject constructor(
    @AdminApolloClient private val adminApolloClient: ApolloClient
) : ProductReviewAdminDataSource {

    override suspend fun createReviewMetaobject(
        productId: String,
        customerId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        createdAt: String,
    ): PocketResult<String, PocketDataError> {
        val fields = listOf(
            MetaobjectFieldInput(key = "product", value = productId),
            MetaobjectFieldInput(key = "customer_id", value = customerId),
            MetaobjectFieldInput(key = "customer_name", value = customerName),
            MetaobjectFieldInput(key = "rating", value = rating.toString()),
            MetaobjectFieldInput(key = "title", value = title),
            MetaobjectFieldInput(key = "body", value = body),
            MetaobjectFieldInput(key = "created_at", value = createdAt),
            MetaobjectFieldInput(key = "approved", value = "true"),
        )
        val input = MetaobjectCreateInput(
            type = "pocket_product_review",
            capabilities = Optional.present(
                MetaobjectCapabilityDataInput(
                    publishable = Optional.present(
                        MetaobjectCapabilityDataPublishableInput(
                            status = MetaobjectStatus.ACTIVE
                        )
                    )
                )
            ),
            fields = Optional.present(fields),
        )

        return when (val result = adminApolloClient
            .mutation(CreateProductReviewMutation(metaobject = input))
            .safeCall()) {
            is PocketResult.Success -> {
                val payload = result.data.metaobjectCreate
                val newId = payload?.metaobject?.id
                if (!payload?.userErrors.isNullOrEmpty() || newId == null) {
                    val errorMsg = payload?.userErrors?.joinToString { "Create Error: ${it.field.orEmpty().joinToString(".")}: ${it.message}" }
                        ?: "Create Error: unknown"
                    payload?.userErrors?.forEach {
                        Log.e("ReviewCreate", "userError: field=${it.field} msg=${it.message} code=${it.code}")
                    }
                    PocketResult.Error(PocketDataError.CustomServerMessage(errorMsg))
                } else {
                    PocketResult.Success(newId)
                }
            }
            is PocketResult.Error -> result
        }
    }

    override suspend fun getLinkedReviewIds(
        productId: String,
    ): PocketResult<List<String>, PocketDataError> {
        return when (val result = adminApolloClient
            .query(GetProductReviewIdsQuery(productId = productId))
            .safeCall()) {
            is PocketResult.Success -> {
                val rawJson = result.data.product?.metafield?.value
                val ids = rawJson?.let {
                    runCatching { Json.decodeFromString<List<String>>(it) }.getOrDefault(emptyList())
                } ?: emptyList()
                PocketResult.Success(ids)
            }
            is PocketResult.Error -> result
        }
    }

    override suspend fun setLinkedReviewIds(
        productId: String,
        reviewIds: List<String>,
    ): PocketResult<Unit, PocketDataError> {
        val idsJson = Json.encodeToString(reviewIds)

        return when (val result = adminApolloClient
            .mutation(SetProductReviewsMutation(productId = productId, reviewIdsJson = idsJson))
            .safeCall()) {
            is PocketResult.Success -> {
                val hasErrors = !result.data.metafieldsSet?.userErrors.isNullOrEmpty()
                if (hasErrors) {
                    val errorMsg = result.data.metafieldsSet.userErrors.joinToString { "Set Error: ${it.field.orEmpty().joinToString(".")}: ${it.message}" }
                        ?: "Set Error: unknown"
                    result.data.metafieldsSet.userErrors.forEach {
                        Log.e("ReviewSet", "userError: field=${it.field} msg=${it.message} code=${it.code}")
                    }
                    PocketResult.Error(PocketDataError.CustomServerMessage(errorMsg))
                } else {
                    PocketResult.Success(Unit)
                }
            }
            is PocketResult.Error -> result
        }
    }

    override suspend fun updateReviewMetaobject(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
    ): PocketResult<Unit, PocketDataError> {
        val fields = listOf(
            MetaobjectFieldInput(key = "customer_name", value = customerName),
            MetaobjectFieldInput(key = "rating", value = rating.toString()),
            MetaobjectFieldInput(key = "title", value = title),
            MetaobjectFieldInput(key = "body", value = body),
            MetaobjectFieldInput(key = "approved", value = "true"),
        )
        val input = MetaobjectUpdateInput(fields = Optional.present(fields))

        return when (val result = adminApolloClient
            .mutation(UpdateProductReviewMutation(reviewId = reviewId, metaobject = input))
            .safeCall()) {
            is PocketResult.Success -> {
                val hasErrors = !result.data.metaobjectUpdate?.userErrors.isNullOrEmpty()
                if (hasErrors) PocketResult.Error(PocketDataError.Remote.SERVER) else PocketResult.Success(Unit)
            }
            is PocketResult.Error -> result
        }
    }

    override suspend fun deleteReviewMetaobject(
        reviewId: String,
    ): PocketResult<Unit, PocketDataError> {
        return when (val result = adminApolloClient
            .mutation(DeleteProductReviewMutation(reviewId = reviewId))
            .safeCall()) {
            is PocketResult.Success -> {
                val payload = result.data.metaobjectDelete
                if (!payload?.userErrors.isNullOrEmpty() || payload?.deletedId == null) {
                    PocketResult.Error(PocketDataError.Remote.SERVER)
                } else {
                    PocketResult.Success(Unit)
                }
            }
            is PocketResult.Error -> result
        }
    }
}
