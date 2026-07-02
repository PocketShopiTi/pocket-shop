package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.tokenmanager.domain.CustomerAccessTokenRepository
import com.iti.pocketshop.core.userdata.UserRepo
import javax.inject.Inject

class ProfileLogOutUseCase @Inject constructor(
    private val tokenRepository: CustomerAccessTokenRepository,
    private val userRepo: UserRepo,
) {
    suspend operator fun invoke(): PocketResult<Unit, PocketDataError.Auth> {
        tokenRepository.clearAndRevoke()
        userRepo.signOut()
        return PocketResult.Success(Unit)
    }
}
