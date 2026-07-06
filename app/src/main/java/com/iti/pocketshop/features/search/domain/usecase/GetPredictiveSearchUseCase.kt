package com.iti.pocketshop.features.search.domain.usecase

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.search.domain.repostory.SearchRepository
import com.iti.pocketshop.features.search.domain.model.PredictiveSearchResult
import jakarta.inject.Inject

class GetPredictiveSearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): PocketResult<PredictiveSearchResult, PocketDataError.Remote> {

        if (query.isBlank()) return PocketResult.Success(PredictiveSearchResult.empty())

        return repository.predictiveSearch(query)
    }
}