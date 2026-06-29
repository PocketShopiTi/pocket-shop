package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileSession
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): PocketResult<ProfileData, PocketDataError> {

        return when (val sessionResult = repository.getUserSession()) {
            is PocketResult.Error ->
                PocketResult.Error(sessionResult.error)

            is PocketResult.Success ->
                when (val session = sessionResult.data) {
                    ProfileSession.Guest -> PocketResult.Success(ProfileData.Guest)

                    is ProfileSession.Authenticated -> loadAuthenticatedProfile(session)
                }
        }
    }

    private suspend fun loadAuthenticatedProfile(
        session: ProfileSession.Authenticated,
    ): PocketResult<ProfileData, PocketDataError> {

        val stats = when (val statsResult = repository.getProfileStats(session.user.id)) {
            is PocketResult.Error -> return PocketResult.Error(statsResult.error)
            is PocketResult.Success -> statsResult.data
        }

        val orders = when (val ordersResult = repository.getRecentOrders(session.user.id)) {
            is PocketResult.Error -> return PocketResult.Error(ordersResult.error)
            is PocketResult.Success -> ordersResult.data
        }

        return PocketResult.Success(
            ProfileData.Authenticated(
                user = session.user,
                stats = stats,
                recentOrders = orders,
            )
        )
    }
}
