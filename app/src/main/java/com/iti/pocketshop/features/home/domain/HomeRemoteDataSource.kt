package com.iti.pocketshop.features.home.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.features.home.domain.models.PromotionAd

interface HomeRemoteSource {
    suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        categoryCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote>

    suspend fun getPromotionAds(): PocketResult<List<PromotionAd>, PocketDataError.Auth>
}
