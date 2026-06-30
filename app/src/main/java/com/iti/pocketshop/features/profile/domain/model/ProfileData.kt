package com.iti.pocketshop.features.profile.domain.model

sealed interface ProfileData {
    data object Guest : ProfileData

    data class Authenticated(
        val user: UserEntity,
        val stats: ProfileStats?,
        val recentOrders: List<OrderEntity>,
    ) : ProfileData
}
