package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails

@Composable
internal fun ProductHeader(
    product: ProductDetails,
    price: Money?,
    compareAtPrice: Money?,
) {
    Text(
        text = product.vendor.uppercase(LocalLocale.current.platformLocale),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Medium,
        ),
    )
    Text(
        text = product.title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 25.sp, lineHeight = 31.sp),
        modifier = Modifier.padding(top = 4.dp),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        PriceColumn(price, compareAtPrice)
    }
}

@Composable
private fun PriceColumn(price: Money?, compareAtPrice: Money?) {
    AnimatedContent(
        targetState = price to compareAtPrice,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "variantPrice",
    ) { (currentPrice, currentCompareAtPrice) ->
        Column(horizontalAlignment = Alignment.End) {
            if (
                currentPrice != null && currentCompareAtPrice != null &&
                currentCompareAtPrice.amount > currentPrice.amount
            ) {
                Text(
                    text = formatMoney(currentCompareAtPrice),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.LineThrough,
                    ),
                )
            }
            Text(
                text = formatMoney(currentPrice),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
            )
        }
    }
}
