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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.Money
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import androidx.compose.ui.res.stringResource
import java.util.Locale

@Composable
internal fun ProductHeader(
    product: ProductDetails,
    price: Money?,
    compareAtPrice: Money?,
) {
    Text(
        text = product.vendor.uppercase(Locale.getDefault()),
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
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        RatingSummary(product.rating, product.reviewCount)
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

@Composable
internal fun RatingSummary(rating: Double, reviewCount: Int) {
    val description = stringResource(R.string.product_details_rating, rating, reviewCount)
    Row(
        modifier = Modifier.semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        RatingStars(rating, starSize = 12.dp)
        Text(
            text = stringResource(R.string.product_details_rating_summary, rating, reviewCount),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        )
    }
}

@Composable
internal fun RatingStars(rating: Double, starSize: androidx.compose.ui.unit.Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index -> StarIcon(filled = index < rating.toInt(), iconSize = starSize) }
    }
}
