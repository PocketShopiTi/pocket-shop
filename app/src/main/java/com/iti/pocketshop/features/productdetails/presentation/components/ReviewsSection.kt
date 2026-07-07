package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    onWriteReview: () -> Unit,
    onEditReview: (ProductReview) -> Unit,
    onDeleteReview: (ProductReview) -> Unit,
    currentUserId: String?,
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
            val hasReviewed = currentUserId != null && reviews.any { it.customerId == currentUserId }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!hasReviewed) {
                    NoRippleTextButton(
                        text = stringResource(R.string.product_details_write_review),
                        onClick = onWriteReview,
                    )
                }
                if (reviewCount > 0) {
                    NoRippleTextButton(
                        text = stringResource(R.string.product_details_see_all_reviews),
                        onClick = onSeeAll,
                    )
                }
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
                ReviewCard(
                    review = review,
                    canManage = review.customerId != null && review.customerId == currentUserId,
                    onEdit = { onEditReview(review) },
                    onDelete = { onDeleteReview(review) },
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}

@Composable
internal fun ReviewCard(
    review: ProductReview,
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                ) {
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
                if (canManage) {
                    ReviewActions(
                        onEdit = onEdit,
                        onDelete = onDelete,
                    )
                }
            }
            if (review.title.isNotBlank()) {
                Text(
                    text = review.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 10.dp),
                )
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

@Composable
private fun RowScope.ReviewActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        IconButton(
            onClick = onEdit,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.product_details_edit_review),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.product_details_delete_review),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

private const val MAX_VISIBLE_REVIEWS = 3
