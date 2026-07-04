package com.iti.pocketshop.features.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.home.domain.models.Product

@Composable
fun ProductRow(
    products: List<Product>,
    favoriteIds: Set<String>,
    onProductClick: (String) -> Unit,
    onWishlistClick: (Product) -> Unit,
    cardWidth: Dp = 200.dp
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(products, key = { it.handle }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                onWishlistClick = onWishlistClick,
                isFavorite = favoriteIds.contains(product.id),
                modifier = Modifier
                    .width(cardWidth)
            )
        }
    }
}