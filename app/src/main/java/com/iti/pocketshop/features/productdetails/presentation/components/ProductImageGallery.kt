package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.productdetails.domain.entity.ProductImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

@Composable
internal fun ProductImageGallery(
    images: List<ProductImage>,
    selectedIndex: Int,
    onImageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        AnimatedContent(
            targetState = images,
            transitionSpec = {
                (fadeIn(tween(260)) + scaleIn(initialScale = 1.04f, animationSpec = tween(260)))
                    .togetherWith(fadeOut(tween(180)))
            },
            contentKey = { list -> list.map { it.id } },
            label = "variantGallery",
        ) { imageList ->
            val isTarget = imageList.map { it.id } == images.map { it.id }
            key(imageList.map { it.id }) {
                GalleryPager(
                    images = imageList,
                    selectedIndex = selectedIndex,
                    onImageSelected = { index -> if (isTarget) onImageSelected(index) },
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    Brush.verticalGradient(
                        0f to MaterialTheme.colorScheme.scrim.copy(alpha = 0.18f),
                        1f to Color.Transparent,
                    ),
                ),
        )
    }
}

@Composable
private fun GalleryPager(
    images: List<ProductImage>,
    selectedIndex: Int,
    onImageSelected: (Int) -> Unit,
) {
    val pageCount = images.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, pageCount - 1),
        pageCount = { pageCount },
    )

    LaunchedEffect(selectedIndex, pageCount) {
        val targetPage = selectedIndex.coerceIn(0, pageCount - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect(onImageSelected)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue.coerceIn(0f, 1f)
            val image = images.getOrNull(page)

            if (image == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.product_details_image_unavailable),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                AsyncImage(
                    model = image.url,
                    contentDescription = image.altText ?: stringResource(
                        R.string.product_details_image,
                        page + 1,
                        images.size,
                    ),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = 1f - (pageOffset * 0.22f)
                            scaleX = 1f - (pageOffset * 0.06f)
                            scaleY = 1f - (pageOffset * 0.06f)
                        },
                )
            }
        }

        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(images.size) { index ->
                    val selected = index == pagerState.currentPage
                    val width by animateDpAsState(
                        targetValue = if (selected) 22.dp else 7.dp,
                        label = "galleryIndicatorWidth",
                    )
                    val color by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                        },
                        label = "galleryIndicatorColor",
                    )
                    Box(
                        modifier = Modifier
                            .size(width = width, height = 7.dp)
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }
        }
    }
}
