package com.iti.pocketshop.common.sessionmanager.data.datasource.customerremote

import com.apollographql.apollo.ApolloClient
import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerCredentials
import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerSession
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.network.map
import com.iti.pocketshop.network.safeCall
import com.iti.pocketshop.shopify.CustomerAccessTokenCreateMutation
import com.iti.pocketshop.shopify.CustomerAccessTokenRenewMutation
import com.iti.pocketshop.shopify.LogoutCustomerMutation
import com.iti.pocketshop.shopify.type.CustomerAccessTokenCreateInput
import java.time.Instant
import javax.inject.Inject

interface ShopifyTokenDataSource {
    suspend fun create(credentials: CustomerCredentials): PocketResult<CustomerSession, PocketDataError>
    suspend fun renewWithAccessToken(session: CustomerSession): PocketResult<CustomerSession, PocketDataError>
    suspend fun revoke(accessToken: String): PocketResult<Unit, PocketDataError.Remote>
}

class ShopifyTokenDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : ShopifyTokenDataSource {

    override suspend fun create(
        credentials: CustomerCredentials,
    ): PocketResult<CustomerSession, PocketDataError> {
        val result = apolloClient.mutation(
            CustomerAccessTokenCreateMutation(
                CustomerAccessTokenCreateInput(
                    email = credentials.email,
                    password = credentials.shopifyPassword,
                )
            )
        ).safeCall()

        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val payload = result.data.customerAccessTokenCreate
                    ?: return PocketResult.Error(PocketDataError.Auth.UNKNOWN)
                val token = payload.customerAccessToken
                    ?: return PocketResult.Error(
                        payload.customerUserErrors.firstOrNull()?.code?.rawValue.toAuthError()
                    )
                token.accessToken.toSession(credentials.uid, token.expiresAt)
            }
        }
    }

    override suspend fun renewWithAccessToken(
        session: CustomerSession,
    ): PocketResult<CustomerSession, PocketDataError> {
        val result = apolloClient.mutation(
            CustomerAccessTokenRenewMutation(session.accessToken)
        ).safeCall()
        return when (result) {
            is PocketResult.Error -> result
            is PocketResult.Success -> {
                val token = result.data.customerAccessTokenRenew?.customerAccessToken
                    ?: return PocketResult.Error(PocketDataError.Auth.UNKNOWN)
                token.accessToken.toSession(session.ownerUid, token.expiresAt)
            }
        }
    }

    override suspend fun revoke(
        accessToken: String,
    ): PocketResult<Unit, PocketDataError.Remote> = apolloClient
        .mutation(LogoutCustomerMutation(accessToken))
        .safeCall()
        .map { }

    private fun String.toSession(
        ownerUid: String,
        expiresAt: String,
    ): PocketResult<CustomerSession, PocketDataError> = runCatching {
        CustomerSession(ownerUid, this, Instant.parse(expiresAt))
    }.fold(
        onSuccess = { PocketResult.Success(it) },
        onFailure = { PocketResult.Error(PocketDataError.Auth.UNKNOWN) },
    )

    private fun String?.toAuthError(): PocketDataError.Auth = when (this) {
        "UNIDENTIFIED_CUSTOMER" -> PocketDataError.Auth.INVALID_CREDENTIALS
        "CUSTOMER_DISABLED" -> PocketDataError.Auth.USER_DISABLED
        else -> PocketDataError.Auth.UNKNOWN
    }
}
