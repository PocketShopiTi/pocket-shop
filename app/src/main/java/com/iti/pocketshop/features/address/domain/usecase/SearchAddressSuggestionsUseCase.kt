package com.iti.pocketshop.features.address.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.AddressLocationSuggestion
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import javax.inject.Inject

class SearchAddressSuggestionsUseCase @Inject constructor(
    private val repository: AddressRepository,
) {
    suspend operator fun invoke(
        query: String,
    ): PocketResult<List<AddressLocationSuggestion>, AddressError> =
        repository.searchSuggestions(query)
}
