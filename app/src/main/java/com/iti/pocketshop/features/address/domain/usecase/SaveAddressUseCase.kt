package com.iti.pocketshop.features.address.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.address.domain.error.AddressError
import com.iti.pocketshop.features.address.domain.model.Address
import com.iti.pocketshop.features.address.domain.model.AddressDraft
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(
    private val repository: AddressRepository,
) {
    suspend operator fun invoke(
        addressId: String? = null,
        draft: AddressDraft,
    ): PocketResult<Address, AddressError>  =
        repository.saveAddress(
            addressId = addressId,
            draft = draft,
        )

}
