package com.iti.pocketshop.features.profile.data

import android.util.Log
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import jakarta.inject.Inject
import java.util.Date

class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {
    override suspend fun getUserData(): PocketResult<UserEntity, PocketDataError.Remote> {
        Log.i("ProfileRepositoryImpl", "Fetching user data")
        return PocketResult.Success(
            UserEntity(
                id = "1",
                name = "Mahmoud ELDemerdash",
                email = "mahmoudeldemerdash5@gmail.com",
                imageUrl = "",
            )
        )
    }

    override suspend fun getProfileStats(userId: String): PocketResult<ProfileStats, PocketDataError.Remote> {
        return PocketResult.Success(
            ProfileStats(
                ordersCount = 10,
                wishListCount = 5,
                addressesCount = 3,
            )
        )
    }

    override suspend fun getRecentOrders(
        userId: String,
        count: Int
    ): PocketResult<List<OrderEntity>, PocketDataError.Remote> {
        return PocketResult.Success(
            listOf(
                OrderEntity(
                    id = "1",
                    status = OrderStatus.SHIPPED,
                    price = 100.0,
                    date = Date(),
                    imageUrl = null
                ),
                OrderEntity(
                    id = "2",
                    status = OrderStatus.PROCESSING,
                    price = 100.0,
                    date = Date(),
                    imageUrl = null
                ),
                OrderEntity(
                    id = "3",
                    status = OrderStatus.DELIVERED,
                    price = 100.0,
                    date = Date(),
                    imageUrl = null
                ),
            )
        )
    }
}