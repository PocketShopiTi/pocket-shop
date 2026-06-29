package com.iti.pocketshop.features.profile.data

import com.google.firebase.auth.FirebaseAuth
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.safeFirebaseCall
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.features.profile.domain.model.ProfileSession
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ProfileRepository {

    override suspend fun getUserSession(): PocketResult<ProfileSession, PocketDataError.Auth> =
        safeFirebaseCall {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null || currentUser.isAnonymous) {
                return@safeFirebaseCall ProfileSession.Guest
            } else {
                return@safeFirebaseCall ProfileSession.Authenticated(
                    currentUser.toUserEntity()
                )
            }
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
                    id = "PK-2026-0847",
                    status = OrderStatus.DELIVERED,
                    total = 778.50,
                    currencyCode = "USD",
                    imageUrl = null,
                ),
                OrderEntity(
                    id = "PK-2026-0612",
                    status = OrderStatus.PROCESSING,
                    total = 249.00,
                    currencyCode = "USD",
                    imageUrl = null,
                ),
                OrderEntity(
                    id = "PK-2026-0481",
                    status = OrderStatus.DELIVERED,
                    total = 437.25,
                    currencyCode = "USD",
                    imageUrl = null,
                ),
            ).take(count)
        )
    }

    override suspend fun signOut(): PocketResult<Unit, PocketDataError.Auth> =
        safeFirebaseCall { firebaseAuth.signOut() }
}