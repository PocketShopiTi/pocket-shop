package com.iti.pocketshop.core.sessionmanager.domain.model


data class UserSession(
    val uid: String,
    val isAnonymous: Boolean,
    val isEmailVerified: Boolean,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val createdAtEpochMillis: Long?,
)
