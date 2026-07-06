package com.iti.pocketshop.features.address.domain.usecase

import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.AddressBook
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(
    private val repository: AddressRepository,
) {
    suspend operator fun invoke(): PocketResult<AddressBook, AddressError> =
        repository.getAddresses()

}
