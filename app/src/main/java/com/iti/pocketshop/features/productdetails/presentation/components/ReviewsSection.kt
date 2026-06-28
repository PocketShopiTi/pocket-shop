package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import java.util.Locale

@Composable
internal fun ReviewsSection(
    reviews: List<ProductReview>,
    reviewCount: Int,
    onSeeAll: () -> Unit,
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.product_details_reviews)
                    .uppercase(Locale.getDefault()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = sectionLabelStyle(),
            )
            if (reviewCount > MAX_VISIBLE_REVIEWS) {
                NoRippleTextButton(
                    text = stringResource(R.string.product_details_see_all_reviews),
                    onClick = onSeeAll,
                )
            }
        }

        if (reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.product_details_no_reviews),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        } else {
            reviews.take(MAX_VISIBLE_REVIEWS).forEach { review ->
                ReviewCard(review, Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ProductReview, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.avatarUrl,
                    contentDescription = review.author,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline),
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = review.author,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RatingStars(review.rating.toDouble(), starSize = 10.dp)
                        Text(
                            text = formatDate(review),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        )
                    }
                }
            }
            Text(
                text = review.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 19.sp),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

private const val MAX_VISIBLE_REVIEWS = 3
