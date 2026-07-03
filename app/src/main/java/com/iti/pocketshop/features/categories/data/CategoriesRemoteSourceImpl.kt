package com.iti.pocketshop.features.categories.data

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.features.categories.domain.CategoriesRemoteSource
import com.iti.pocketshop.features.categories.domain.models.CategoryItem
import com.iti.pocketshop.shopify.GetCategoriesQuery
import javax.inject.Inject

class CategoriesRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : CategoriesRemoteSource {
    override suspend fun getCategories(first: Int): PocketResult<List<CategoryItem>, PocketDataError.Remote> {
        return apolloClient
            .query(GetCategoriesQuery(first = first))
            .safeCall()
            .map { data ->
                data.collections.nodes.map { it.toDomain() }
            }
    }
}
