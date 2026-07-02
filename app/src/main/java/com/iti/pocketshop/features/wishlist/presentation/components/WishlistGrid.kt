package com.iti.pocketshop.features.wishlist.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct


@Composable
fun WishlistGrid(
    favorites: List<FavoriteProduct>,
    openProductDetails: (String) -> Unit,
    onToggleFavorite: (FavoriteProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = favorites,
            key = { it.id }
        ) { product ->
            WishlistProductCard(
                product = product,
                openProductDetails = openProductDetails,
                onToggleFavorite = { onToggleFavorite(product) },
                modifier = Modifier.animateItem()
            )
        }
    }
}