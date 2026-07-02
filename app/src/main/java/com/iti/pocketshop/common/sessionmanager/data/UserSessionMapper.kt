package com.iti.pocketshop.common.sessionmanager.data

import com.google.firebase.auth.FirebaseUser
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession

fun FirebaseUser.toUserSession(): UserSession = UserSession(
    uid = uid,
    isAnonymous = isAnonymous,
    isEmailVerified = isEmailVerified,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString(),
    createdAtEpochMillis = metadata?.creationTimestamp,
)
