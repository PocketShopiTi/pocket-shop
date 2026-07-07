package com.iti.pocketshop.features.home.data

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.home.domain.HomeRemoteSource
import com.iti.pocketshop.features.home.domain.HomeRepository
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteSource: HomeRemoteSource,
) : HomeRepository {

    override suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        brandCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote> {
        return remoteSource.getHomeData(
            featuredCount = featuredCount,
            bestSellerCount = bestSellerCount,
            brandCount = brandCount,
            newArrivalsCount = newArrivalsCount,
        )
    }

    override suspend fun getPromotionAds(): PocketResult<List<PromotionAd>, PocketDataError.Auth> {
        return remoteSource.getPromotionAds()
    }
}
