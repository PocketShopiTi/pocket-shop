package com.iti.pocketshop.features.profile.presentation

import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity

data class ProfileState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val user: UserEntity? = null,
    val isGuest: Boolean = false,
    val userStats: ProfileStats? = null,
    val orders: List<OrderEntity> = emptyList(),
)