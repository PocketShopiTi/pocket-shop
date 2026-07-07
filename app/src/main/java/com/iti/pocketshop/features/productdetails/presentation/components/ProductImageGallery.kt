package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    var expandedImageIndex by remember(images) { mutableStateOf<Int?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        GalleryPager(
            images = images,
            selectedIndex = selectedIndex,
            onImageSelected = onImageSelected,
            onImageClicked = { index -> expandedImageIndex = index },
        )
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

    expandedImageIndex
        ?.takeIf { it in images.indices }
        ?.let { imageIndex ->
            ExpandedImageViewer(
                image = images[imageIndex],
                imageIndex = imageIndex,
                imageCount = images.size,
                onDismiss = { expandedImageIndex = null },
            )
        }
}

@Composable
private fun GalleryPager(
    images: List<ProductImage>,
    selectedIndex: Int,
    onImageSelected: (Int) -> Unit,
    onImageClicked: (Int) -> Unit,
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
                        .tapWithoutConsumingDrag { onImageClicked(page) }
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

private fun Modifier.tapWithoutConsumingDrag(onTap: () -> Unit): Modifier = pointerInput(onTap) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        val up = waitForUpOrCancellation()
        if (up != null) {
            onTap()
        }
    }
}

@Composable
private fun ExpandedImageViewer(
    image: ProductImage,
    imageIndex: Int,
    imageCount: Int,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        var scale by remember(image.id) { mutableStateOf(1f) }
        var offset by remember(image.id) { mutableStateOf(Offset.Zero) }
        var containerSize by remember { mutableStateOf(IntSize.Zero) }
        val transformableState = rememberTransformableState { _, zoomChange, panChange, _ ->
            val nextScale = (scale * zoomChange).coerceIn(1f, 5f)
            offset = if (nextScale == 1f) {
                Offset.Zero
            } else {
                val maxX = containerSize.width * (nextScale - 1f) / 2f
                val maxY = containerSize.height * (nextScale - 1f) / 2f
                Offset(
                    x = (offset.x + panChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + panChange.y).coerceIn(-maxY, maxY),
                )
            }
            scale = nextScale
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { containerSize = it }
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.92f)),
        ) {
            AsyncImage(
                model = image.url,
                contentDescription = image.altText ?: stringResource(
                    R.string.product_details_image,
                    imageIndex + 1,
                    imageCount,
                ),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(image.id) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f
                                    offset = Offset.Zero
                                } else {
                                    scale = 2.5f
                                }
                            },
                        )
                    }
                    .transformable(transformableState)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.74f)),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.dismiss),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
