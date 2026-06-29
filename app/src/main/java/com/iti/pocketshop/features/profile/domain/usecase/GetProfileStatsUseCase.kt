package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileStatsUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(userId: String): PocketResult<ProfileStats, PocketDataError.Remote> {
        return repository.getProfileStats(userId)
    }
}
