package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.tokenmanager.domain.CustomerAccessTokenRepository
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileLoadUpdate
import com.iti.pocketshop.features.profile.domain.model.ProfileSession
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
    private val tokenRepository: CustomerAccessTokenRepository,
) {
    operator fun invoke(): Flow<ProfileLoadUpdate> = flow {
        when (val sessionResult = repository.getUserSession()) {
            is PocketResult.Error -> emit(ProfileLoadUpdate.Failed(sessionResult.error))
            is PocketResult.Success -> when (val session = sessionResult.data) {
                ProfileSession.Guest -> emit(ProfileLoadUpdate.Cached(ProfileData.Guest))
                is ProfileSession.Authenticated -> {
                    emit(
                        ProfileLoadUpdate.Cached(
                            ProfileData.Authenticated(
                                user = session.user,
                                stats = null,
                                recentOrders = emptyList(),
                            )
                        )
                    )
                    when (val shopifySession = tokenRepository.getValidToken()) {
                        is PocketResult.Error ->
                            emit(ProfileLoadUpdate.Failed(shopifySession.error))

                        is PocketResult.Success -> when (
                            val profileResult = repository.getProfile(
                                shopifySession.data.accessToken
                            )
                        ) {
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
}
