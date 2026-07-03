package com.iti.pocketshop.features.address.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.AddressLocationDetails
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import javax.inject.Inject

class ResolveAddressSuggestionUseCase @Inject constructor(
    private val repository: AddressRepository,
) {
    suspend operator fun invoke(
        placeId: String,
    ): PocketResult<AddressLocationDetails, AddressError> =
          repository.resolveSuggestion(placeId)

}
