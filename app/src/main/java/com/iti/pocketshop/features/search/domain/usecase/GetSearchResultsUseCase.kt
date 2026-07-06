package com.iti.pocketshop.features.search.domain.usecase

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.search.domain.repostory.SearchRepository
import com.iti.pocketshop.features.search.domain.model.SearchResult
import jakarta.inject.Inject


class GetSearchResultsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(
        query: String,
        first: Int = 12,
        after: String? = null,
        filters: List<String>? = null,
        sortKey: String? = null,
        reverse: Boolean? = null
    ): PocketResult<SearchResult, PocketDataError.Remote> {

        return repository.search(query, first, after, filters, sortKey, reverse)
    }
}