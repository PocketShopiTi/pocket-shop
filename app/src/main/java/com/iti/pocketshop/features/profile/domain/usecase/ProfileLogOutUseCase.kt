package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileLogOutUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): PocketResult<Unit, PocketDataError.Auth> =
        repository.logout()

}
