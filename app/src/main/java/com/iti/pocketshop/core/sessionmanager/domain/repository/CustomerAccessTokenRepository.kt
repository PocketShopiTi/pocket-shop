package com.iti.pocketshop.core.sessionmanager.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.sessionmanager.domain.model.CustomerSession

interface CustomerAccessTokenRepository {
    suspend fun getValidToken(): PocketResult<CustomerSession, PocketDataError>

    suspend fun clearAndRevoke(): PocketResult<Unit, PocketDataError.Auth>
}