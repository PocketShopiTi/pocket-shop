package com.iti.pocketshop.features.search.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.core.di.StorefrontApolloClient
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.core.networkutils.safeCall
import com.iti.pocketshop.features.search.data.mapper.toDomain
import com.iti.pocketshop.features.search.data.mapper.toProductFilter
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.shopify.PredictiveSearchQuery
import com.iti.pocketshop.shopify.SearchQuery
import com.iti.pocketshop.shopify.type.SearchSortKeys
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    @param:StorefrontApolloClient
    private val apolloClient: ApolloClient
) : SearchRemoteDataSource {

    override suspend fun predictiveSearch(
        query: String
    ): PocketResult<PredictiveSearchResult, PocketDataError.Remote> {
        return apolloClient
            .query(PredictiveSearchQuery(query = query))
            .safeCall()
            .map { data -> data.toDomain() }
    }

    override suspend fun search(
        query: String,
        first: Int,
        after: String?,
        filters: List<String>?,
        sortKey: String?,
        reverse: Boolean?
    ): PocketResult<SearchResult, PocketDataError.Remote> {
        return apolloClient
            .query(
                SearchQuery(
                    query = query,
                    first = first,
                    after = Optional.presentIfNotNull(after),
                    filters = Optional.presentIfNotNull(filters?.mapNotNull { it.toProductFilter() }),
                    sortKey = Optional.presentIfNotNull(sortKey?.let { SearchSortKeys.safeValueOf(it) }),
                    reverse = Optional.presentIfNotNull(reverse)
                )
            )
            .safeCall()
            .map { data -> data.toDomain() }
    }
}