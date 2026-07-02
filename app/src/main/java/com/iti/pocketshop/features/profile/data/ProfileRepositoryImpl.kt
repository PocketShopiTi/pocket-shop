package com.iti.pocketshop.features.profile.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.map
import com.iti.pocketshop.features.profile.data.datasource.firebase.FirebaseDataSource
import com.iti.pocketshop.features.profile.data.datasource.shopify.ShopifyDataSource
import com.iti.pocketshop.features.profile.data.mapper.toProfileData
import com.iti.pocketshop.features.profile.data.mapper.toUserEntity
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileSession
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val shopifyDataSource: ShopifyDataSource,
) : ProfileRepository {

    override suspend fun getUserSession(): PocketResult<ProfileSession, PocketDataError.Auth> =
        firebaseDataSource.getUserSession().map { user ->
            if (user == null || user.isAnonymous) {
                ProfileSession.Guest
            } else {
                ProfileSession.Authenticated(user.toUserEntity())
            }
        }

    override suspend fun getProfile(
        accessToken: String,
        orderCount: Int,
    ): PocketResult<ProfileData.Authenticated, PocketDataError> {
        return when (
            val profileResult = shopifyDataSource.getUserProfile(
                accessToken = accessToken,
                ordersCount = orderCount,
            )
        ) {
            is PocketResult.Error -> profileResult
            is PocketResult.Success -> {
                val customer = profileResult.data
                    ?: return PocketResult.Error(PocketDataError.Auth.UnAuthorized)
                PocketResult.Success(customer.toProfileData())
            }
        }
    }
}
