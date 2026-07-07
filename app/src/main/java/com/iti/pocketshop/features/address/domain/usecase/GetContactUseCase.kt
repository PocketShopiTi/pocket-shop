package com.iti.pocketshop.features.address.domain.usecase

import android.net.Uri
import com.iti.pocketshop.features.address.domain.model.ContactInfo
import com.iti.pocketshop.features.address.domain.repository.AddressRepository
 import javax.inject.Inject

class GetContactUseCase @Inject constructor(
    private val contactsRepository: AddressRepository,
) {
    suspend operator fun invoke(uri: Uri): ContactInfo? = contactsRepository.getContact(uri)
}