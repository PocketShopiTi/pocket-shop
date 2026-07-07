package com.iti.pocketshop.features.home.domain

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.home.domain.models.HomeData
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(
        featuredCount: Int = 10,
        bestSellerCount: Int = 10,
        newArrivalsCount: Int = 10,
        brandCount: Int = 10,
    ): PocketResult<HomeData, PocketDataError.Remote> {
        return repository.getHomeData(
            featuredCount = featuredCount,
            bestSellerCount = bestSellerCount,
            brandCount = brandCount,
            newArrivalsCount = newArrivalsCount,
        )
    }
}

class GetPromotionAdsUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): PocketResult<List<PromotionAd>, PocketDataError.Auth> =
          repository.getPromotionAds()

}
