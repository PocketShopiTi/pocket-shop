package com.iti.pocketshop.features.profile.domain.model

data class UserEntity(
    val id: String,
    val name: String,
    val email: String,
    val imageUrl: String?,
)
