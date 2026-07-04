package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.productdetails.domain.entity.ProductDetails
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsAction
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsState

@Composable
internal fun ProductContent(
    product: ProductDetails,
    state: ProductDetailsState,
    onAction: (ProductDetailsAction) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        item {
            ProductImageGallery(
                images = product.images,
                selectedIndex = state.selectedImageIndex,
                onImageSelected = { index ->
                    onAction(ProductDetailsAction.ImageSelected(index))
                },
            )
        }
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                ProductHeader(
                    product = product,
                    price = state.selectedVariant?.price,
                    compareAtPrice = state.selectedVariant?.compareAtPrice,
                )

                if (product.options.isNotEmpty()) {
                    SectionDivider()
                    product.options.forEach { option ->
                        ProductOptionSelector(option, state, onAction)
                    }
                }

                if (product.description.isNotBlank()) {
                    SectionDivider()
                    DescriptionSection(
                        description = product.description,
                        expanded = state.isDescriptionExpanded,
                        onToggle = { onAction(ProductDetailsAction.ToggleDescription) },
                    )
                }

                SectionDivider()
                ReviewsSection(
                    reviews = product.reviews,
                    reviewCount = product.reviewCount,
                    onSeeAll = { onAction(ProductDetailsAction.SeeAllReviewsClicked) },
                )
            }
        }
    }
}
