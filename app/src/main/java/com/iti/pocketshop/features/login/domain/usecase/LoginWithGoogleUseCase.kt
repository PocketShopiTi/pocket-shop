package com.iti.pocketshop.features.login.domain.usecase

import com.iti.pocketshop.features.login.domain.mapper.LoginResult
import com.iti.pocketshop.features.login.domain.repository.LoginRepository

import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(idToken: String): LoginResult = repository.loginWithGoogle(idToken)
}