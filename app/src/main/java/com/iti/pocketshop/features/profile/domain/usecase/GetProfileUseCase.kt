package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileLoadUpdate
import com.iti.pocketshop.features.profile.domain.model.ProfileSession
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<ProfileLoadUpdate> = flow {
        val sessionResult = repository.getUserSession()

        when (sessionResult) {
            is PocketResult.Error -> emit(ProfileLoadUpdate.Failed(sessionResult.error))

            is PocketResult.Success ->
                when (sessionResult.data) {
                    ProfileSession.Guest -> emit(ProfileLoadUpdate.Cached(ProfileData.Guest))

                    is ProfileSession.Authenticated -> {
                        emit(
                            ProfileLoadUpdate.Cached(
                                ProfileData.Authenticated(
                                    user = sessionResult.data.user,
                                    stats = null,
                                    recentOrders = listOf(),
                                )
                            )
                        )

                        val profileResult = repository.getProfile()
                        when (profileResult) {
                            is PocketResult.Success ->
                                emit(ProfileLoadUpdate.Fresh(profileResult.data))

                            is PocketResult.Error ->
                                emit(ProfileLoadUpdate.Failed(profileResult.error))
                        }
                    }
                }
        }
    }
}
