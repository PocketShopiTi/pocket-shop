package com.iti.pocketshop.features.profile.data.datasource.shopify

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.shopify.GetProfileQuery

interface ShopifyDataSource {
    suspend fun getUserProfile(
        accessToken: String,
        ordersCount: Int,
    ): PocketResult<GetProfileQuery.Customer?, PocketDataError.Remote>
}
