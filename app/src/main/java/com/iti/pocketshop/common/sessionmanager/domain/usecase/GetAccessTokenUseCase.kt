package com.iti.pocketshop.common.sessionmanager.domain.usecase

import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    private val repository: CustomerAccessTokenRepository
) {
    suspend operator fun invoke(): PocketResult<String, PocketDataError> =
        when (val tokenResult = repository.getValidToken()) {
            is PocketResult.Error -> tokenResult
            is PocketResult.Success -> PocketResult.Success(tokenResult.data.accessToken)
        }

}