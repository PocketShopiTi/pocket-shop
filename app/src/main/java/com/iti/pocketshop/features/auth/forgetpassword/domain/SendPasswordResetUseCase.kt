package com.iti.pocketshop.features.auth.forgetpassword.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import javax.inject.Inject

class SendPasswordResetUseCase @Inject constructor(
    private val repository: SharedAuthRepository,
) {
    suspend operator fun invoke(email: String): PocketResult<Unit, PocketDataError.Auth> =
        when (val result = repository.sendPasswordReset(email.trim())) {
            is PocketResult.Error -> if (result.error == PocketDataError.Auth.USER_NOT_FOUND) {
                PocketResult.Success(Unit)
            } else result

            is PocketResult.Success -> result
        }
}