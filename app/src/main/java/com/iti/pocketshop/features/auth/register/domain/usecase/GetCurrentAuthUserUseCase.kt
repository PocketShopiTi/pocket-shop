package com.iti.pocketshop.features.auth.register.domain.usecase

import com.iti.pocketshop.features.auth.register.domain.model.AuthUser
import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import javax.inject.Inject

class GetCurrentAuthUserUseCase @Inject constructor(
    private val repository: SharedAuthRepository,
) {
    operator fun invoke(): AuthUser? = repository.currentUser()
}