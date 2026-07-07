package com.iti.pocketshop.features.productdetails.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.common.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.features.productdetails.domain.entity.ProductReview
import com.iti.pocketshop.features.productdetails.presentation.components.ArrowBackIcon
import com.iti.pocketshop.features.productdetails.presentation.components.ErrorContent
import com.iti.pocketshop.features.productdetails.presentation.components.LoadingContent
import com.iti.pocketshop.features.productdetails.presentation.components.NoRippleTextButton
import com.iti.pocketshop.features.productdetails.presentation.components.RatingStars
import com.iti.pocketshop.features.productdetails.presentation.components.ReviewCard
import com.iti.pocketshop.features.productdetails.presentation.components.ReviewEditorSheet

@Composable
fun ProductReviewsScreen(
    state: ProductDetailsState,
    currentUserId: String?,
    defaultReviewCustomerName: String,
    onAction: (ProductDetailsAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(innerPadding))
            state.errorMessage != null || state.product == null -> ErrorContent(
                modifier = Modifier.padding(innerPadding),
                onRetry = { onAction(ProductDetailsAction.Retry) },
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
            ) {
                item {
                    ReviewsTopBar(onBack = { onAction(ProductDetailsAction.BackClicked) })
                    ReviewsSummary(
                        averageRating = state.product.rating,
                        reviews = state.product.reviews,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.product_details_reviews),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        val hasReviewed = currentUserId != null && state.product.reviews.any { it.customerId == currentUserId }
                        if (!hasReviewed) {
                            NoRippleTextButton(
                                text = stringResource(R.string.product_details_write_review),
                                onClick = {
                                    onAction(ProductDetailsAction.WriteReviewClicked(defaultReviewCustomerName))
                                },
                            )
                        }
                    }
                }
                if (state.product.reviews.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.product_details_no_reviews),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                    }
                } else {
                    items(state.product.reviews.size) { index ->
                        val review = state.product.reviews[index]
                        ReviewCard(
                            review = review,
                            canManage = review.customerId != null && review.customerId == currentUserId,
                            onEdit = { onAction(ProductDetailsAction.EditReviewClicked(review)) },
                            onDelete = { onAction(ProductDetailsAction.DeleteReviewClicked(review)) },
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                }
            }
        }
    }

    if (state.isReviewEditorVisible) {
        ReviewEditorSheet(
            editingReview = state.editingReview,
            initialCustomerName = state.reviewCustomerName,
            customerId = currentUserId.orEmpty(),
            isSubmitting = state.reviewActionInProgress,
            onAction = onAction,
        )
    }

    state.reviewToDelete?.let {
        AlertDialog(
            onDismissRequest = { onAction(ProductDetailsAction.DeleteReviewDismissed) },
            title = { Text(text = stringResource(R.string.product_details_delete_review_title)) },
            text = { Text(text = stringResource(R.string.product_details_delete_review_message)) },
            confirmButton = {
                Button(
                    onClick = { onAction(ProductDetailsAction.DeleteReviewConfirmed) },
                    enabled = !state.reviewActionInProgress,
                ) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(ProductDetailsAction.DeleteReviewDismissed) },
                    enabled = !state.reviewActionInProgress,
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ReviewsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            ArrowBackIcon()
        }
        Text(
            text = stringResource(R.string.product_details_reviews),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun ReviewsSummary(
    averageRating: Double,
    reviews: List<ProductReview>,
) {
    val total = reviews.size.coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = averageRating.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displaySmall,
            )
            RatingStars(averageRating, starSize = 16.dp)
            Text(
                text = stringResource(R.string.product_details_rating_count, reviews.size),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (5 downTo 1).forEach { star ->
                val count = reviews.count { it.rating == star }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = star.toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    LinearProgressIndicator(
                        progress = { count / total.toFloat() },
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = count.toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

private fun ProductDetailsAction.requiresSignedInUser(): Boolean {
    return this is ProductDetailsAction.WriteReviewClicked ||
        this is ProductDetailsAction.EditReviewClicked ||
        this is ProductDetailsAction.DeleteReviewClicked ||
        this is ProductDetailsAction.ReviewSubmitted ||
        this == ProductDetailsAction.DeleteReviewConfirmed
}

private fun UserSession?.defaultReviewCustomerName(): String {
    return this?.displayName
        ?.takeIf { it.isNotBlank() }
        ?: this?.email
            ?.substringBefore("@")
            ?.takeIf { it.isNotBlank() }
        ?: ""
}
