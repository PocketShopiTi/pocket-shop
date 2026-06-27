package com.iti.pocketshop.features.login.domain.usecase

import com.iti.pocketshop.features.login.domain.mapper.LoginResult
import com.iti.pocketshop.features.login.domain.repository.LoginRepository

import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(email: String, password: String): LoginResult {
       // if (!isValidEmail(email)) return LoginResult.Error("Invalid email")
        if (password.length < 6) return LoginResult.Error("Password too short")
        return repository.loginWithEmail(email, password)
    }
}



