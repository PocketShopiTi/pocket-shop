package com.iti.pocketshop.features.login.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val avatarUrl: String?
)