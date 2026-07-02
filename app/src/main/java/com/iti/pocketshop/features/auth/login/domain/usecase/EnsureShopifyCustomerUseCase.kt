package com.iti.pocketshop.features.auth.login.domain.usecase

import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import com.iti.pocketshop.features.auth.register.domain.repository.RegisterRepository
import javax.inject.Inject

class EnsureShopifyCustomerUseCase @Inject constructor(
    private val repository: RegisterRepository,
) {
    suspend operator fun invoke(
        user: AuthUser,
        isNewGoogleUser: Boolean = false,
    ) = if (isNewGoogleUser) {
        repository.createShopifyCustomer(user)
    } else {
        repository.ensureShopifyCustomer(user)
    }
}