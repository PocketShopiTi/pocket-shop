package com.iti.pocketshop.features.wishlist.presentation.state

import androidx.compose.runtime.Immutable
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct

@Immutable
data class WishlistState(
    val isLoading: Boolean = false,
    val favorites: List<FavoriteProduct> = emptyList(),
)