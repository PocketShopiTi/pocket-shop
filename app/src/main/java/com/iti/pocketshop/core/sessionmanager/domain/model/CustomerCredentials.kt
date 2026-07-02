package com.iti.pocketshop.core.sessionmanager.domain.model

data class CustomerCredentials(
    val uid: String,
    val email: String,
    val shopifyPassword: String,
)