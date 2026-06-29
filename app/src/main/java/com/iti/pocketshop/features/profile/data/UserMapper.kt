package com.iti.pocketshop.features.profile.data

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.features.profile.domain.model.UserEntity

fun FirebaseUser.toUserEntity(): UserEntity {
    return UserEntity(
        id = uid,
        name = displayName ?: "User Name",
        email = email ?: "",
        imageUrl = photoUrl?.toString()
    )
}