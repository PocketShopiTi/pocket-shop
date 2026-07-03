package com.iti.pocketshop.features.auth.register.domain.model

data class AuthData(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
) {
    val fullName: String = "$firstName $lastName".trim()
}
