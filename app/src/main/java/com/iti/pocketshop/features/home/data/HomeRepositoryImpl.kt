package com.iti.pocketshop.features.home.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.home.domain.HomeRemoteSource
import com.iti.pocketshop.features.home.domain.HomeRepository
import com.iti.pocketshop.features.home.domain.models.HomeData
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteSource: HomeRemoteSource,
) : HomeRepository {

    override suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        categoryCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote> {
        return remoteSource.getHomeData(
            featuredCount = featuredCount,
            bestSellerCount = bestSellerCount,
            categoryCount = categoryCount,
            newArrivalsCount = newArrivalsCount,
        )
    }
}