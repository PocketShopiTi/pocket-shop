package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetCurrentUserDataUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): PocketResult<UserEntity, PocketDataError.Remote> {
        return repository.getUserData()
    }
}
