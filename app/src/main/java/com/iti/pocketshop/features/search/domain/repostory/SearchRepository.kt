package com.iti.pocketshop.features.search.domain.repostory

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import com.iti.pocketshop.features.search.domain.model.SearchResult

interface SearchRepository {
    suspend fun predictiveSearch(query: String): PocketResult<PredictiveSearchResult, PocketDataError.Remote>

    suspend fun search(
        query: String,
        first: Int,
        after: String? = null,
        filters: List<String>? = null,
        sortKey: String? = null,
        reverse: Boolean? = null
    ): PocketResult<SearchResult, PocketDataError.Remote>
}