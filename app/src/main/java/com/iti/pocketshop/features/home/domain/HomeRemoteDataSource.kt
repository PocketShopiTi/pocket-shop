package com.iti.pocketshop.features.home.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.home.domain.models.HomeData

interface HomeRemoteSource {
    suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        brandCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote>
}