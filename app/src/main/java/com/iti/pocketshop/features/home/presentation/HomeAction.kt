package com.iti.pocketshop.features.home.presentation

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.features.home.domain.models.PromotionAd

sealed interface HomeAction {
    data object FetchData: HomeAction
    data class ToggleFavorite(val product: FavoriteProduct): HomeAction
    data class OpenPromotionAd(val ad: PromotionAd): HomeAction
    data object ClosePromotionAd: HomeAction
}
