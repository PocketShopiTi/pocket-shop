package com.iti.pocketshop.core.tokenmanager.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.tokenmanager.domain.model.CustomerSession

interface CustomerAccessTokenRepository {
    suspend fun getValidToken(): PocketResult<CustomerSession, PocketDataError>
    
    suspend fun clearAndRevoke(): PocketResult<Unit, PocketDataError.Auth>
}