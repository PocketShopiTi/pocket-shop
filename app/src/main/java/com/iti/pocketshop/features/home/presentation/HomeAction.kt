package com.iti.pocketshop.features.home.presentation

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct

sealed interface HomeAction {
    data object FetchData: HomeAction
    data class ToggleFavorite(val product: FavoriteProduct): HomeAction
}