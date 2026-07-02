package com.iti.pocketshop.features.auth.register.domain.model

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isAnonymous: Boolean,
    val isEmailVerified: Boolean,
)

data class RegistrationRecord(
    val shopifyPassword: String,
    val shopifyCustomerId: String?,
)
