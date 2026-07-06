package com.iti.pocketshop.core.userdata

import com.iti.pocketshop.common.sessionmanager.domain.usecase.GetAccessTokenUseCase
import com.iti.pocketshop.network.PocketResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildConfigCustomerAccessTokenProvider @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : CustomerAccessTokenProvider {
    override suspend fun currentCustomerAccessToken(): String? {
        return when (val result = getAccessTokenUseCase()) {
            is PocketResult.Success -> result.data.takeIf { it.isNotBlank() }
            is PocketResult.Error -> null
        }
    }
}
