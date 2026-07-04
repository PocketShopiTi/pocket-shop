package com.iti.pocketshop.common.sessionmanager.domain.repository

import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerSession
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface CustomerAccessTokenRepository {
    suspend fun getValidToken(): PocketResult<CustomerSession, PocketDataError>

    suspend fun clearAndRevoke(): PocketResult<Unit, PocketDataError.Auth>
}