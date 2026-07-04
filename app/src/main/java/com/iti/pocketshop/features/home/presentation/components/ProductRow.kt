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
import com.iti.pocketshop.features.home.presentation.models.UIProduct

@Composable
fun ProductRow(
    products: List<UIProduct>,
    onProductClick: (String) -> Unit,
    onWishlistClick: (UIProduct) -> Unit,
    cardWidth: Dp = 200.dp
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = onProductClick,
                onWishlistClick = onWishlistClick,
                modifier = Modifier
                    .width(cardWidth)
            )
        }
    }
}
