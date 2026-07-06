package com.iti.pocketshop.features.auth.register.data.datasource.firebase

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
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
        cartId: String? = null
    ): PocketResult<Unit, PocketDataError.Auth>
    
    suspend fun saveCartId(
        uid: String,
        cartId: String
    ): PocketResult<Unit, PocketDataError.Auth>

    suspend fun loadCartId(
        uid: String
    ): PocketResult<String?, PocketDataError.Auth>
}