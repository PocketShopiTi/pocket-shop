package com.iti.pocketshop.features.register.domain.usecase


import com.iti.pocketshop.features.register.domain.repo.AuthRepository
import javax.inject.Inject

/**
 * Executes the business logic and validation for registration.
 * @Inject tells Hilt how to create this class.
 */

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        val trimmedEmail = email.trim()

        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (password.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters."))
        }

        return authRepository.register(trimmedEmail, password)
    }
}