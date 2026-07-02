package com.iti.pocketshop.core.tokenmanager.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.tokenmanager.data.datasource.local.CustomerTokenStore
import com.iti.pocketshop.core.tokenmanager.data.datasource.remote.CustomerCredentialsDataSource
import com.iti.pocketshop.core.tokenmanager.data.datasource.remote.ShopifyTokenDataSource
import com.iti.pocketshop.core.tokenmanager.domain.CustomerAccessTokenRepository
import com.iti.pocketshop.core.tokenmanager.domain.model.CustomerCredentials
import com.iti.pocketshop.core.tokenmanager.domain.model.CustomerSession
import java.time.Clock
import java.time.Duration
import javax.inject.Inject

class CustomerAccessTokenRepositoryImpl @Inject constructor(
    private val credentialsDataSource: CustomerCredentialsDataSource,
    private val tokenDataSource: ShopifyTokenDataSource,
    private val tokenStore: CustomerTokenStore,
    private val clock: Clock,
) : CustomerAccessTokenRepository {

    private companion object {
        val REFRESH_WINDOW: Duration = Duration.ofDays(3)
    }

    override suspend fun getValidToken(): PocketResult<CustomerSession, PocketDataError> {
        val credentials = when (val result = credentialsDataSource.load()) {
            is PocketResult.Error -> return result
            is PocketResult.Success -> result.data
        }
        val now = clock.instant()
        val stored = tokenStore.read()?.takeIf { it.ownerUid == credentials.uid }
        if (stored == null) tokenStore.clear()

        if (stored == null || !stored.expiresAt.isAfter(now)) {
            return createAndSave(credentials)
        }

        val remaining = Duration.between(now, stored.expiresAt)
        if (remaining < REFRESH_WINDOW) {
            return when (val renewed = tokenDataSource.renew(stored)) {
                is PocketResult.Success -> renewed.also { tokenStore.save(it.data) }
                is PocketResult.Error -> if (renewed.error is PocketDataError.Remote) {
                    PocketResult.Success(stored)
                } else {
                    createAndSave(credentials)
                }
            }
        }

        return PocketResult.Success(stored)
    }

    override suspend fun clearAndRevoke(): PocketResult<Unit, PocketDataError.Auth> {
        tokenStore.read()?.accessToken?.let { tokenDataSource.revoke(it) }
        tokenStore.clear()
        return PocketResult.Success(Unit)
    }

    private suspend fun createAndSave(
        credentials: CustomerCredentials,
    ): PocketResult<CustomerSession, PocketDataError> = when (
        val created = tokenDataSource.create(credentials)
    ) {
        is PocketResult.Error -> created
        is PocketResult.Success -> created.also { tokenStore.save(it.data) }
    }


}

