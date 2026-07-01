package com.iti.pocketshop.features.profile.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.shopify.GetProfileQuery
import java.time.Instant

fun FirebaseUser.toUserEntity(): UserEntity =
    UserEntity(
        id = uid,
        name = displayName.orEmpty(),
        email = email.orEmpty(),
        imageUrl = photoUrl?.toString(),
        memberSinceEpochMillis = metadata?.creationTimestamp,
    )


fun GetProfileQuery.Customer.toProfileData(): ProfileData.Authenticated =
    ProfileData.Authenticated(
        user = toUserEntity(),
        stats = toProfileStats(),
        recentOrders = toOrdersList(),
    )

private fun GetProfileQuery.Customer.toProfileStats(): ProfileStats = ProfileStats(
    ordersCount = numberOfOrders.toInt(),
    wishListCount = 0,
    addressesCount = addresses.edges.size
)


private fun GetProfileQuery.Customer.toUserEntity(): UserEntity =
    UserEntity(
        id = id,
        name = displayName,
        email = email.orEmpty(),
        imageUrl = avatarUrl,
        memberSinceEpochMillis = createdAt.toEpochMillisOrNull(),
    )

private fun String.toEpochMillisOrNull(): Long? =
    runCatching { Instant.parse(this).toEpochMilli() }.getOrNull()
