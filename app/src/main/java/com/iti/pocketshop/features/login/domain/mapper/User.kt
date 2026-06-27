package com.iti.pocketshop.features.login.domain.mapper

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val avatarUrl: String?
)