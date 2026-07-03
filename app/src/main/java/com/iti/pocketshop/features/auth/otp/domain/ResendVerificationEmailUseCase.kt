package com.iti.pocketshop.features.auth.otp.domain

import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import javax.inject.Inject

class ResendVerificationEmailUseCase @Inject constructor(
    private val repository: SharedAuthRepository,
) {
    suspend operator fun invoke() = repository.resendVerificationEmail()
}