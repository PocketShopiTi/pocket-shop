package com.iti.pocketshop.features.auth.login.domain.usecase

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.auth.login.domain.model.LoginOutcome
import com.iti.pocketshop.features.auth.login.domain.repository.LoginRepository
import javax.inject.Inject

class ContinueAsGuestUseCase @Inject constructor(
    private val repository: LoginRepository,
) {
    suspend operator fun invoke(): PocketResult<LoginOutcome, PocketDataError.Auth> =
        when (val result = repository.continueAsGuest()) {
            is PocketResult.Error -> result
            is PocketResult.Success -> PocketResult.Success(LoginOutcome.Ready)
        }
}
