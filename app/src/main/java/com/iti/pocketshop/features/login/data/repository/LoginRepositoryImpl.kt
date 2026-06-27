package com.iti.pocketshop.features.login.data.repository

import com.iti.pocketshop.features.login.domain.repository.LoginRepository
import com.iti.pocketshop.features.login.domain.mapper.LoginResult
import com.iti.pocketshop.features.login.domain.mapper.User
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(

) : LoginRepository {
    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): LoginResult {
        return  LoginResult.Success(User(id= "ahmed@goaj.com", email = "askdlgjl", name = null, avatarUrl = null))
    }

    override suspend fun loginWithGoogle(): LoginResult {
        return  LoginResult.Success(User(id= "ahmed@goaj.com", email = "askdlgjl", name = null, avatarUrl = null))
    }

    override suspend fun continueAsGuest(): LoginResult {
        return  LoginResult.Success(User(id= "ahmed@goaj.com", email = "askdlgjl", name = null, avatarUrl = null))
    }


}