package com.iti.pocketshop.features.profile.domain.repository

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileData

interface ProfileRepository {
    suspend fun getProfile(
        accessToken: String,
        orderCount: Int = 3,
    ): PocketResult<ProfileData.Authenticated, PocketDataError>
}
