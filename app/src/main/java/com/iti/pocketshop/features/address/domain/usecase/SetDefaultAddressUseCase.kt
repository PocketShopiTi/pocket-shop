package com.iti.pocketshop.features.address.domain.usecase

import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import javax.inject.Inject

class SetDefaultAddressUseCase @Inject constructor(
    private val repository: AddressRepository,
) {
    suspend operator fun invoke(addressId: String): PocketResult<Unit, AddressError> =
        repository.setDefaultAddress(addressId)

}
