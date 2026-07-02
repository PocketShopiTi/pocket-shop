package com.iti.pocketshop.features.wishlist.presentation.action

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct

sealed interface WishlistAction {
    data object FetchFavorites: WishlistAction
    data class ToggleFavorite(val product: FavoriteProduct): WishlistAction
}