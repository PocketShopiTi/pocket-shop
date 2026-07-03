package com.iti.pocketshop.features.auth.shared

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.repository.SharedAuthRepository
import javax.inject.Inject

class CheckEmailVerificationUseCase @Inject constructor(
    private val repository: SharedAuthRepository,
) {
    suspend operator fun invoke(): PocketResult<Boolean, PocketDataError.Auth> =
        when (val result = repository.reloadCurrentUser()) {
            is PocketResult.Error -> result
            is PocketResult.Success -> PocketResult.Success(result.data.isEmailVerified)
        }
}