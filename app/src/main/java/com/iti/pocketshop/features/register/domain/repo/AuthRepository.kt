package com.iti.pocketshop.features.register.domain.repo

interface AuthRepository {
    suspend fun register(email: String, password: String): Result<Unit>
}