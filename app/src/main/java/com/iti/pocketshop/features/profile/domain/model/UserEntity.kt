package com.iti.pocketshop.features.profile.domain.model

import com.iti.pocketshop.core.utils.toFormattedDate

data class UserEntity(
    val id: String,
    val name: String,
    val email: String,
    val imageUrl: String?,
    private val memberSinceEpochMillis: Long? = null,
) {
    val memberSince: String? = memberSinceEpochMillis?.toFormattedDate("MMMM yyyy")
}
