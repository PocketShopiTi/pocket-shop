package com.iti.pocketshop.features.auth.register.data.datasource.firebase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.auth.register.domain.model.RegistrationRecord

interface RegistrationFirestoreDataSource {
    suspend fun load(
        uid: String
    ): PocketResult<RegistrationRecord?, PocketDataError.Auth>

    suspend fun saveUser(
        uid: String,
        email: String,
        firstName: String,
        lastName: String,
        customerId: String?,
        password: String,
    ): PocketResult<Unit, PocketDataError.Auth>
}