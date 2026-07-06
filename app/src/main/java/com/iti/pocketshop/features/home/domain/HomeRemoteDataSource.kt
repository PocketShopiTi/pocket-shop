package com.iti.pocketshop.features.home.domain

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.features.home.domain.models.PromotionAd

interface HomeRemoteSource {
    suspend fun getHomeData(
        featuredCount: Int,
        bestSellerCount: Int,
        newArrivalsCount: Int,
        brandCount: Int,
    ): PocketResult<HomeData, PocketDataError.Remote>

    suspend fun getPromotionAds(): PocketResult<List<PromotionAd>, PocketDataError.Auth>
}
