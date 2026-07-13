package com.iti.pocketshop.features.home.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HeroBanner(
    ads: List<PromotionAd>,
    onAdClick: (PromotionAd) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (ads.isEmpty()) {
        StaticHeroBanner(modifier = modifier)
        return
    }

    val colors = LocalExtendedColors.current
    val pagerState = rememberPagerState(pageCount = { ads.size })
    val coroutineScope = rememberCoroutineScope()

    // Auto-advance, paused while the user is actively dragging/settling.
    LaunchedEffect(pagerState, ads.size) {
        if (ads.size <= 1) return@LaunchedEffect
        while (true) {
            delay(BANNER_AUTO_SCROLL_DELAY_MILLIS.milliseconds)
            if (!pagerState.isScrollInProgress) {
                val next = (pagerState.currentPage + 1) % ads.size
                pagerState.animateScrollToPage(
                    page = next,
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(top = 8.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val ad = ads[page]
            val pageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            PromotionAdContent(
                ad = ad,
                onClick = { onAdClick(ad) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .graphicsLayer {
                        translationX = -pageOffset * size.width * 0.15f
                        alpha = lerp(0.4f, 1f, 1f - abs(pageOffset).coerceIn(0f, 1f))
                    },
            )
        }
        if (ads.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
            ) {
                HeroBannerDots(
                    pageCount = ads.size,
                    currentPage = pagerState.currentPage,
                    onDotClick = { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun HeroBannerDots(
    pageCount: Int,
    currentPage: Int,
    onDotClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 18.dp else 6.dp,
                animationSpec = tween(250, easing = FastOutSlowInEasing),
                label = "dotWidth",
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) colors.primary else colors.outline,
                animationSpec = tween(250),
                label = "dotColor",
            )
            Box(
                modifier = Modifier
                    .size(width = width, height = 6.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onDotClick(index) },
            )
        }
    }
}

@Composable
private fun PromotionAdContent(
    ad: PromotionAd,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "promotionBannerMotion")
    val imageScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BANNER_IMAGE_MOTION_DELAY_MILLIS,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "promotionImageScale",
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxSize()
    ) {
        Box{
            AsyncImage(
                model = ad.imageUrl,
                contentDescription = ad.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = imageScale; scaleY = imageScale },
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colors.surface.copy(alpha = 0.42f),
                                colors.surface.copy(alpha = 0.12f),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = ad.couponCode,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = ad.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(
                        text = ad.description,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }


                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 4.dp,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        text = ad.buttonText.ifBlank { stringResource(R.string.shop_now) },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun StaticHeroBanner(
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp)),
    ) {
        AsyncImage(
            model = R.drawable.hero_banner_placeholder,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colors.surface.copy(alpha = 0.7f),
                            colors.surface.copy(alpha = 0.1f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(28.dp),
        ) {
            Surface(
                color = colors.primary.copy(alpha = 0.14f),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.big_sale),
                    color = colors.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.up_to_50_off),
                color = colors.textPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

private const val BANNER_AUTO_SCROLL_DELAY_MILLIS = 3_500L
private const val BANNER_IMAGE_MOTION_DELAY_MILLIS = 4_800