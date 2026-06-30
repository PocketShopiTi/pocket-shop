package com.iti.pocketshop.features.profile.data.datasource.shopify

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.shopify.GetProfileQuery

interface ShopifyDataSource {

    suspend fun getCustomerAccessToken(): PocketResult<String, PocketDataError.Auth>

    suspend fun getUserProfile(accessToken: String, ordersCount: Int):
            PocketResult<GetProfileQuery.Customer?, PocketDataError.Remote>

    suspend fun logOut(accessToken: String): PocketResult<Unit, PocketDataError.Remote>

}
