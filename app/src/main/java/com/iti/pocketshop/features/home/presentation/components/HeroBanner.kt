package com.iti.pocketshop.features.home.presentation.components
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import kotlinx.coroutines.delay
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

    var currentIndex by remember(ads) { mutableStateOf(0) }

    LaunchedEffect(currentIndex, ads.size) {
        if (ads.size > 1) {
            delay(BANNER_AUTO_SCROLL_DELAY_MILLIS.milliseconds)
            currentIndex = (currentIndex + 1) % ads.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(ads.size) {
                if (ads.size > 1) {
                    var accumulatedDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { accumulatedDrag = 0f },
                        onHorizontalDrag = { change, delta ->
                            change.consume()
                            accumulatedDrag += delta
                        },
                        onDragEnd = {
                            when {
                                accumulatedDrag <= -SWIPE_THRESHOLD_PX ->
                                    currentIndex = (currentIndex + 1) % ads.size

                                accumulatedDrag >= SWIPE_THRESHOLD_PX ->
                                    currentIndex = (currentIndex - 1 + ads.size) % ads.size
                            }
                            accumulatedDrag = 0f
                        },
                    )
                }
            },
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                (slideInHorizontally(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetX = { width -> width },
                ) + fadeIn(tween(300))).togetherWith(
                    (slideOutHorizontally(
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                        targetOffsetX = { width -> -width },
                    ) + fadeOut(tween(500)) + scaleOut(
                        targetScale = 1.2f,
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ))
                ).using(
                    SizeTransform(clip = false)
                )
            },
            label = "promotionAdContent",
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            val ad = ads[index.coerceIn(ads.indices)]
            PromotionAdContent(
                ad = ad,
                onClick = { onAdClick(ad) },
            )
        }
    }
}

@Composable
private fun PromotionAdContent(
    ad: PromotionAd,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "promotionBannerMotion")
    val imageScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BANNER_IMAGE_MOTION_DELAY_MILLIS,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "promotionImageScale",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ad.imageUrl,
            contentDescription = ad.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScale
                    scaleY = imageScale
                },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.62f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(24.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = ad.couponCode,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = ad.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (ad.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = ad.description,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            ) {
                Text(
                    text = ad.buttonText.ifBlank { stringResource(R.string.shop_now) },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun StaticHeroBanner(
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.35f),
                            primaryColor.copy(alpha = 0f),
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(28.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.big_sale),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.up_to_50_off),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

private const val BANNER_AUTO_SCROLL_DELAY_MILLIS = 3_500L
private const val BANNER_IMAGE_MOTION_DELAY_MILLIS = 4_800
private const val SWIPE_THRESHOLD_PX = 100f