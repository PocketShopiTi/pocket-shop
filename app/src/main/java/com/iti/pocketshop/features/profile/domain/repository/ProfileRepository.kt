package com.iti.pocketshop.features.profile.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileSession

interface ProfileRepository {
    suspend fun getUserSession(): PocketResult<ProfileSession, PocketDataError.Auth>
    suspend fun getProfile(
        accessToken: String,
        orderCount: Int = 3,
    ): PocketResult<ProfileData.Authenticated, PocketDataError>
}
