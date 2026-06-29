package com.iti.pocketshop.features.login.domain.usecase

import com.iti.pocketshop.features.login.domain.model.LoginResult
import com.iti.pocketshop.features.login.domain.repository.LoginRepository

import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(email: String, password: String): LoginResult {
        return repository.loginWithEmail(email, password)
    }
}



