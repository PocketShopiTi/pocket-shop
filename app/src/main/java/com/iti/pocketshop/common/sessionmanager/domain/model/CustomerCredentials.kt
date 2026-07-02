package com.iti.pocketshop.common.sessionmanager.domain.model

data class CustomerCredentials(
    val uid: String,
    val email: String,
    val shopifyPassword: String,
)