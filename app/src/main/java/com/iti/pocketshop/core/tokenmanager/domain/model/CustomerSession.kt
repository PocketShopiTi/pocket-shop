package com.iti.pocketshop.core.tokenmanager.domain.model

import java.time.Instant

data class CustomerSession(
    val ownerUid: String,
    val accessToken: String,
    val expiresAt: Instant,
)

