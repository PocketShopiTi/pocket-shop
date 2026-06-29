package com.iti.pocketshop.features.profile.domain.repository

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity

interface ProfileRepository {

    suspend fun getUserData(): PocketResult<UserEntity, PocketDataError.Remote>

    suspend fun getProfileStats(userId: String): PocketResult<ProfileStats, PocketDataError.Remote>

    suspend fun getRecentOrders(
        userId: String,
        count: Int = 3
    ): PocketResult<List<OrderEntity>, PocketDataError.Remote>

}