package com.iti.pocketshop.features.home.domain

import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.features.home.domain.models.PromotionAd

interface HomeRepository {
    suspend fun getHomeData(
        featuredCount: Int = 10,
        bestSellerCount: Int = 10,
        newArrivalsCount: Int = 10,
        brandCount: Int = 10,
    ): PocketResult<HomeData, PocketDataError.Remote>

    suspend fun getPromotionAds(): PocketResult<List<PromotionAd>, PocketDataError.Auth>
}
