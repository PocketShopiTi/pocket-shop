package com.iti.pocketshop.common.sessionmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.iti.pocketshop.common.sessionmanager.data.datasource.customerremote.CustomerCredentialsDataSource
import com.iti.pocketshop.common.sessionmanager.data.datasource.customerremote.ShopifyTokenDataSource
import com.iti.pocketshop.common.sessionmanager.data.datasource.local.CustomerTokenStore
import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerCredentials
import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerSession
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.common.sessionmanager.domain.repository.CustomerAccessTokenRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Clock
import java.time.Duration
import javax.inject.Inject

class CustomerAccessTokenRepositoryImpl @Inject constructor(
    private val credentialsDataSource: CustomerCredentialsDataSource,
    private val tokenDataSource: ShopifyTokenDataSource,
    private val tokenStore: CustomerTokenStore,
    private val auth: FirebaseAuth,
) : CustomerAccessTokenRepository {

    private companion object {
        val REFRESH_WINDOW: Duration = Duration.ofDays(3)
    }

    private val mutex = Mutex()

    override suspend fun getValidToken(): PocketResult<CustomerSession, PocketDataError> =
        mutex.withLock {
            getValidTokenInternal()
        }

    suspend fun getValidTokenInternal(): PocketResult<CustomerSession, PocketDataError> {
        val user = auth.currentUser
        if (user == null || user.isAnonymous) {
            return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
        }

        var customerSession = tokenStore.read()?.takeIf { it.ownerUid == user.uid }

        if (shouldRefreshToken(customerSession)) {
            val credentials = when (val result = getCredentials()) {
                is PocketResult.Error -> return result
                is PocketResult.Success -> result.data
            }

            customerSession =
                when (val session = createAndSaveToken(credentials)) {
                    is PocketResult.Error -> return session
                    is PocketResult.Success -> session.data
                }

            if (shouldRefreshToken(customerSession))
                return PocketResult.Error(PocketDataError.Auth.TOKEN_NOT_VALID)
        }

        return PocketResult.Success(customerSession!!)
    }

    override suspend fun clearAndRevoke(): PocketResult<Unit, PocketDataError.Auth> {
        val accessToken = tokenStore.read()?.accessToken
        tokenStore.clear()
        accessToken?.let { tokenDataSource.revoke(it) }
        return PocketResult.Success(Unit)
    }

    private fun shouldRefreshToken(customerSession: CustomerSession?): Boolean {
        if (customerSession == null) return true

        val remaining = Duration.between(Clock.systemUTC().instant(), customerSession.expiresAt)

        return remaining < REFRESH_WINDOW
    }

    suspend fun getCredentials(): PocketResult<CustomerCredentials, PocketDataError> =
        when (val result = credentialsDataSource.load()) {
            is PocketResult.Error -> result
            is PocketResult.Success -> PocketResult.Success(result.data)
        }

    suspend fun createAndSaveToken(credentials: CustomerCredentials): PocketResult<CustomerSession, PocketDataError> {
        val session = tokenDataSource.create(credentials)

        return when (session) {
            is PocketResult.Error -> tokenDataSource.create(credentials)
            is PocketResult.Success -> session.also { tokenStore.save(it.data) }
        }
    }

}