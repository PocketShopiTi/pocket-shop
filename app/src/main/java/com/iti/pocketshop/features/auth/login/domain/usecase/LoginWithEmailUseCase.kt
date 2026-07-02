package com.iti.pocketshop.features.auth.login.domain.usecase

import android.util.Log
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.sessionmanager.domain.repository.CustomerAccessTokenRepository
import com.iti.pocketshop.features.auth.login.domain.model.LoginOutcome
import com.iti.pocketshop.features.auth.login.domain.repository.LoginRepository
import com.iti.pocketshop.features.auth.shared.CheckEmailVerificationUseCase
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val checkEmailVerification: CheckEmailVerificationUseCase,
    private val ensureShopifyCustomer: EnsureShopifyCustomerUseCase,
    private val tokenRepository: CustomerAccessTokenRepository,
) {
    private val TAG = "LoginWithEmailUseCase"
    suspend operator fun invoke(
        email: String,
        password: String,
    ): PocketResult<LoginOutcome, PocketDataError> {
        val login = repository.loginWithEmail(email.trim(), password)
        Log.i(TAG, "invoke: loginWithEmail = $login")
        val user = when (login) {
            is PocketResult.Error -> return login
            is PocketResult.Success -> login.data
        }
        val verification = checkEmailVerification()
        Log.i(TAG, "invoke: email verification = $verification")

        when (verification) {
            is PocketResult.Error -> return verification
            is PocketResult.Success -> if (!verification.data) {
                return PocketResult.Success(LoginOutcome.NeedsEmailVerification)
            }
        }
        val provisioning = ensureShopifyCustomer(user)
        Log.i(TAG, "invoke: ensureShopifyCustomer = $provisioning")
        when (provisioning) {
            is PocketResult.Error -> return provisioning
            is PocketResult.Success -> Unit
        }

        val token = tokenRepository.getValidToken()
        Log.i(TAG, "invoke: getValidCustomerAccessToken = $token")
        return when (token) {
            is PocketResult.Error -> token
            is PocketResult.Success -> PocketResult.Success(LoginOutcome.Ready)
        }
    }
}
