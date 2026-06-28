package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import com.iti.pocketshop.features.productdetails.presentation.ProductDetailsAction
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun ProductImageGallery(
    images: List<ProductImage>,
    selectedIndex: Int,
    isFavorite: Boolean,
    onAction: (ProductDetailsAction) -> Unit,
) {
    val pageCount = images.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, pageCount - 1),
        pageCount = { pageCount },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { onAction(ProductDetailsAction.ImageSelected(it)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val image = images.getOrNull(page)
            AsyncImage(
                model = image?.url,
                contentDescription = image?.altText ?: stringResource(
                    R.string.product_details_image,
                    page + 1,
                    images.size,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RoundIconButton(
                contentDescription = stringResource(R.string.product_details_back),
                onClick = { onAction(ProductDetailsAction.BackClicked) },
            ) { ArrowBackIcon() }
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = { onAction(ProductDetailsAction.ToggleFavorite) },
            )
        }
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (index == pagerState.currentPage) 20.dp else 6.dp,
                                height = 6.dp,
                            )
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "favoriteScale",
    )
    val color by animateColorAsState(
        targetValue = if (isFavorite) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface,
        label = "favoriteColor",
    )
    RoundIconButton(
        contentDescription = stringResource(
            if (isFavorite) R.string.product_details_remove_favorite
            else R.string.product_details_add_favorite,
        ),
        onClick = onClick,
    ) {
        Box(Modifier.graphicsLayer(scaleX = scale, scaleY = scale)) {
            HeartIcon(filled = isFavorite, color = color)
        }
    }
}

@Composable
private fun RoundIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .semantics { this.contentDescription = contentDescription }
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shadowElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) { icon() }
    }
}
