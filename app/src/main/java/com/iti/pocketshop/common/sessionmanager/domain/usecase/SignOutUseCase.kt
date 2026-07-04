package com.iti.pocketshop.common.sessionmanager.domain.usecase

import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.common.sessionmanager.domain.repository.UserRepo
import com.iti.pocketshop.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val tokenRepository: CustomerAccessTokenRepository,
    private val userRepository: UserRepo,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke() {
        tokenRepository.clearAndRevoke()
        userRepository.signOut()
        cartRepository.clearLocalCart()
    }
}
