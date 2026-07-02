package com.iti.pocketshop.features.auth.login.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.tokenmanager.domain.CustomerAccessTokenRepository
import com.iti.pocketshop.features.auth.login.domain.model.LoginOutcome
import com.iti.pocketshop.features.auth.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val ensureShopifyCustomer: EnsureShopifyCustomerUseCase,
    private val tokenRepository: CustomerAccessTokenRepository,
) {
    suspend operator fun invoke(idToken: String): PocketResult<LoginOutcome, PocketDataError> {
        val user = when (val login = repository.loginWithGoogle(idToken)) {
            is PocketResult.Error -> return login
            is PocketResult.Success -> login.data
        }
        when (val provisioning = ensureShopifyCustomer(user)) {
            is PocketResult.Error -> return provisioning
            is PocketResult.Success -> Unit
        }
        
        return when (val token = tokenRepository.getValidToken()) {
            is PocketResult.Error -> token
            is PocketResult.Success -> PocketResult.Success(LoginOutcome.Ready)
        }
    }
}
