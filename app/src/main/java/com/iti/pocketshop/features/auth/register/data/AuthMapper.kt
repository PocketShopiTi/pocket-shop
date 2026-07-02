package com.iti.pocketshop.features.auth.register.data

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.features.auth.register.domain.model.AuthUser


fun FirebaseUser.toDomain() = AuthUser(
    uid = uid,
    email = email.orEmpty(),
    displayName = displayName,
    isAnonymous = isAnonymous,
    isEmailVerified = isEmailVerified,
)