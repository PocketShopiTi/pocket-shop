package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.common.sessionmanager.domain.usecase.GetAccessTokenUseCase
import com.iti.pocketshop.common.sessionmanager.domain.usecase.GetCurrentUserSessionUseCase
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileLoadUpdate
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
    private val getAccessToken: GetAccessTokenUseCase,
    private val getCurrentUserSession: GetCurrentUserSessionUseCase,
) {
    operator fun invoke(): Flow<ProfileLoadUpdate> = flow {
        val session = getCurrentUserSession()
        if (session == null || session.isAnonymous) {
            emit(ProfileLoadUpdate.Cached(ProfileData.Guest))
        } else {
            emit(
                ProfileLoadUpdate.Cached(
                    ProfileData.Authenticated(
                        user = session.toUserEntity(),
                        stats = null,
                        recentOrders = emptyList(),
                    )
                )
            )
            when (val shopifySession = getAccessToken()) {
                is PocketResult.Error ->
                    emit(ProfileLoadUpdate.Failed(shopifySession.error))

                is PocketResult.Success -> when (
                    val profileResult = repository.getProfile(
                        shopifySession.data
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

    private fun UserSession.toUserEntity(): UserEntity = UserEntity(
        id = uid,
        name = displayName.orEmpty(),
        email = email.orEmpty(),
        imageUrl = photoUrl,
        memberSinceEpochMillis = createdAtEpochMillis,
    )
}
