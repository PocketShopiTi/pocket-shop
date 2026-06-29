package com.iti.pocketshop.features.profile.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.ProfileSession
import com.iti.pocketshop.features.profile.domain.model.ProfileStats

interface ProfileRepository {

    suspend fun getUserSession(): PocketResult<ProfileSession, PocketDataError.Auth>

    suspend fun getProfileStats(userId: String): PocketResult<ProfileStats, PocketDataError.Remote>

    suspend fun getRecentOrders(
        userId: String,
        count: Int = 3
    ): PocketResult<List<OrderEntity>, PocketDataError.Remote>

    suspend fun signOut(): PocketResult<Unit, PocketDataError.Auth>

}
