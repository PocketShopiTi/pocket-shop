package com.iti.pocketshop.features.search.data.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.search.data.datasource.SearchRemoteDataSource
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResult
import com.iti.pocketshop.features.search.domain.repostory.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteSource: SearchRemoteDataSource
) : SearchRepository {

    override suspend fun predictiveSearch(
        query: String
    ): PocketResult<PredictiveSearchResult, PocketDataError.Remote> {
        return remoteSource.predictiveSearch(query)
    }

    override suspend fun search(
        query: String,
        first: Int,
        after: String?,
        filters: List<String>?,
        sortKey: String?,
        reverse: Boolean?
    ): PocketResult<SearchResult, PocketDataError.Remote> {
        return remoteSource.search(
            query = query,
            first = first,
            after = after,
            filters = filters,
            sortKey = sortKey,
            reverse = reverse
        )
    }
}