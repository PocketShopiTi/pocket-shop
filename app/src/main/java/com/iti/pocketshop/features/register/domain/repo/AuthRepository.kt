package com.iti.pocketshop.features.register.domain.repo

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface AuthRepository {
    suspend fun register(email: String, password: String): PocketResult<Unit, PocketDataError>
}