package com.iti.pocketshop.features.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.home.domain.models.Brand

@Composable
fun BrandRow(
    brands: List<Brand>,
    onBrandClick: (Brand) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(brands, key = { it.id }) { brand ->
            BrandChip(
                brand = brand,
                onClick = { onBrandClick(brand) },
                modifier = Modifier.animateItem()
            )
        }
    }
}
