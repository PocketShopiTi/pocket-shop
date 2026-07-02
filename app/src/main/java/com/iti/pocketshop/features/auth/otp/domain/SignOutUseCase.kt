package com.iti.pocketshop.features.auth.otp.domain

import com.iti.pocketshop.core.tokenmanager.domain.CustomerAccessTokenRepository
import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: SharedAuthRepository,
    private val tokenRepository: CustomerAccessTokenRepository,
) {
    suspend operator fun invoke() {
        tokenRepository.clearAndRevoke()
        authRepository.signOut()
    }
}