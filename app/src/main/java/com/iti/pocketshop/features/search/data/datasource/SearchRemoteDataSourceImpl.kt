package com.iti.pocketshop.features.search.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
import com.iti.pocketshop.network.safeCall
import com.iti.pocketshop.features.search.data.mapper.toDomain
import com.iti.pocketshop.features.search.data.mapper.toProductFilter
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.shopify.PredictiveSearchQuery
import com.iti.pocketshop.shopify.SearchQuery
import com.iti.pocketshop.shopify.type.SearchSortKeys
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
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