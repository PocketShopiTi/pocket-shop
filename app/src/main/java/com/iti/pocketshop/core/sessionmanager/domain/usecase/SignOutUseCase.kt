package com.iti.pocketshop.core.sessionmanager.domain.usecase

import com.iti.pocketshop.core.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.core.sessionmanager.domain.repository.UserRepo
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val tokenRepository: CustomerAccessTokenRepository,
    private val userRepository: UserRepo,
) {
    suspend operator fun invoke() {
        tokenRepository.clearAndRevoke()
        userRepository.signOut()
    }
}
