package com.iti.pocketshop.features.profile.data

import com.iti.pocketshop.CUSTOMER_ACCESS_TOKEN
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
    private val shopifyDataSource: ShopifyDataSource
) : ProfileRepository {

    override suspend fun getUserSession(): PocketResult<ProfileSession, PocketDataError.Auth> {
        return firebaseDataSource.getUserSession().map { user ->
            if (user == null || user.isAnonymous) {
                ProfileSession.Guest
            } else {
                ProfileSession.Authenticated(user.toUserEntity())
            }
        }
    }

    override suspend fun getProfile(
        orderCount: Int,
    ): PocketResult<ProfileData.Authenticated, PocketDataError> {
        val tokenResult = getCustomerAccessToken()
        
        return when (tokenResult) {
            is PocketResult.Error -> PocketResult.Error(tokenResult.error)

            is PocketResult.Success -> {
                val profileResult = shopifyDataSource.getUserProfile(
                    accessToken = tokenResult.data,
                    ordersCount = orderCount,
                )

                when (profileResult) {
                    is PocketResult.Error -> PocketResult.Error(profileResult.error)

                    is PocketResult.Success -> {
                        val customer = profileResult.data
                            ?: return PocketResult.Error(PocketDataError.Auth.UnAuthorized)

                        PocketResult.Success(customer.toProfileData())
                    }
                }
            }
        }
    }

    private fun getCustomerAccessToken(): PocketResult<String, PocketDataError.Auth> =
        PocketResult.Success(CUSTOMER_ACCESS_TOKEN)


    override suspend fun logout(): PocketResult<Unit, PocketDataError.Auth> {
        val token = when (val tokenResult = getCustomerAccessToken()) {
            is PocketResult.Success -> tokenResult.data
            is PocketResult.Error -> null
        }
        token?.let {
            shopifyDataSource.logOut(accessToken = token)
        }

        //todo:also delete the locally stored access token

        firebaseDataSource.logout()

        return PocketResult.Success(Unit)
    }
}