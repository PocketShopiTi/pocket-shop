package com.iti.pocketshop.features.address.domain.usecase

import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.AddressLocationDetails
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import javax.inject.Inject

class ReverseGeocodeLocationUseCase @Inject constructor(
    private val repository: AddressRepository,
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): PocketResult<AddressLocationDetails, AddressError> = repository.reverseGeocode(latitude, longitude)

}
