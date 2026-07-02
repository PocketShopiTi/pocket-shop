package com.iti.pocketshop.features.register.domain.usecase


import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.register.domain.repo.AuthRepository
import javax.inject.Inject


class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): PocketResult<Unit, PocketDataError> {
        val trimmedEmail = email.trim()

        return authRepository.register(trimmedEmail, password)
    }
}