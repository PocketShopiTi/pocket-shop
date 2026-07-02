package com.iti.pocketshop.common.sessionmanager.domain.usecase

import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.common.sessionmanager.domain.repository.UserRepo
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
