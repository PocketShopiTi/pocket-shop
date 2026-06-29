package com.iti.pocketshop.features.profile.data

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.features.profile.domain.model.UserEntity

fun FirebaseUser.toUserEntity(): UserEntity =
    UserEntity(
        id = uid,
        name = displayName.orEmpty(),
        email = email.orEmpty(),
        imageUrl = photoUrl?.toString(),
        memberSinceEpochMillis = metadata?.creationTimestamp,
    )