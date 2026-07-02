package com.iti.pocketshop.features.auth.register.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.repository.RegisterRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: RegisterRepository,
) {
    suspend operator fun invoke(data: AuthData): PocketResult<Unit, PocketDataError> =
        repository.register(
            data.copy(
                email = data.email.trim(),
                firstName = data.firstName.trim(),
                lastName = data.lastName.trim(),
            )
        )
}

