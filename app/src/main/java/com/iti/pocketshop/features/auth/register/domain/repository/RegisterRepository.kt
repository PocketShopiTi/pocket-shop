package com.iti.pocketshop.features.auth.register.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.AuthData
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser

interface RegisterRepository {
    suspend fun register(data: AuthData): PocketResult<Unit, PocketDataError>

    suspend fun ensureShopifyCustomer(user: AuthUser): PocketResult<Unit, PocketDataError>
}
