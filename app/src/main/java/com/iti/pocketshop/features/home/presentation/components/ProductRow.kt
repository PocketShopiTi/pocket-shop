package com.iti.pocketshop.features.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.home.presentation.models.UIProduct

import com.iti.pocketshop.core.tutorial.tutorialTarget

@Composable
fun ProductRow(
    products: List<UIProduct>,
    onProductClick: (String) -> Unit,
    onWishlistClick: (UIProduct) -> Unit,
    cardWidth: Dp = 200.dp,
    targetFirstItem: Boolean = false
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(products.size, key = { products[it].id }) { index ->
            val product = products[index]
            ProductCard(
                product = product,
                onClick = onProductClick,
                onWishlistClick = onWishlistClick,
                modifier = Modifier
                    .width(cardWidth)
                    .then(
                        if (targetFirstItem && index == 0) Modifier.tutorialTarget(1)
                        else Modifier
                    )
            )
        }
    }
}
