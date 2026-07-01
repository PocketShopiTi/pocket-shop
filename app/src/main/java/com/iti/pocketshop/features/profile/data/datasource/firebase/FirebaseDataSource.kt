package com.iti.pocketshop.features.profile.data.datasource.firebase

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult

interface FirebaseDataSource {
    suspend fun getUserSession(): PocketResult<FirebaseUser?, PocketDataError.Auth>

    suspend fun logout(): PocketResult<Unit, PocketDataError.Auth>
}