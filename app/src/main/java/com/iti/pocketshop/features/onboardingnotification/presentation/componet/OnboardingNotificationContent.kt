package com.iti.pocketshop.features.onboardingnotification.presentation.componet

import android.content.ClipData
import android.content.ClipboardManager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.network.toUserMessage
import com.iti.pocketshop.features.onboardingnotification.domain.model.NotificationAd
import com.iti.pocketshop.features.onboardingnotification.presentation.OnboardingNotificationAction
import com.iti.pocketshop.features.onboardingnotification.presentation.OnboardingNotificationState
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@Composable
fun OnboardingNotificationContent(
    state: OnboardingNotificationState,
    onAction: (OnboardingNotificationAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            when {
                state.isLoading -> PromoLoadingContent()
                state.error != null -> PromoErrorContent(
                    message = state.error.toUserMessage(LocalContext.current),
                    onRetry = { onAction(OnboardingNotificationAction.Retry) },
                    onHome = { onAction(OnboardingNotificationAction.Home) },
                )
                state.isUnavailable -> PromoUnavailableContent(
                    onHome = { onAction(OnboardingNotificationAction.Home) },
                )
                state.ad?.active == true -> PromoPagerContent(
                    ad = state.ad,
                    state = state,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun PromoPagerContent(
    ad: NotificationAd,
    state: OnboardingNotificationState,
    onAction: (OnboardingNotificationAction) -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = { OnboardingNotificationState.PAGE_COUNT },
    )

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != state.currentPage) {
            onAction(OnboardingNotificationAction.SwipePage(pagerState.settledPage))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 56.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            val rawOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val distance = abs(rawOffset).coerceIn(0f, 1f)
            val scale = 1f - (distance * 0.08f)
            val alpha = 1f - (distance * 0.38f)
            val offsetY = distance * 36f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        this.translationY = offsetY
                    },
                contentAlignment = Alignment.Center,
            ) {
                when (page) {
                    0 -> PromoIntroPage(ad)
                    1 -> PromoDetailsPage(ad)
                    2 -> PromoCouponPage(ad)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(
                R.string.notification_onboarding_page_indicator,
                state.currentPage + 1,
                OnboardingNotificationState.PAGE_COUNT,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (state.isLastPage) {
                    onAction(OnboardingNotificationAction.Home)
                } else {
                    onAction(OnboardingNotificationAction.Next)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = if (state.isLastPage) {
                    ad.buttonText.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.shop_now)
                } else {
                    stringResource(R.string.onboarding_next)
                },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
  fun PromoIntroPage(ad: NotificationAd) {
    val infiniteTransition = rememberInfiniteTransition(label = "promoIntroFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "promoIntroFloatY",
    )

    AnimatedPromoColumn {
        PromoImage(
            imageUrl = ad.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .graphicsLayer {
                    translationY = floatOffset
                },
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = ad.title,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.notification_onboarding_intro_subtitle),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun PromoDetailsPage(ad: NotificationAd) {
    val infiniteTransition = rememberInfiniteTransition(label = "promoDetailsPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "promoDetailsPulseScale",
    )

    AnimatedPromoColumn {
        Icon(
            imageVector = Icons.Outlined.LocalOffer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(56.dp)
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.notification_onboarding_details_title),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(24.dp),
        ) {
            Text(
                text = ad.description,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PromoCouponPage(ad: NotificationAd) {
    var couponVisible by remember(ad.id) { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "couponPulse")
    val couponScale by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "couponPulseScale",
    )

    LaunchedEffect(ad.id) {
        delay(250.milliseconds)
        couponVisible = true
    }

    AnimatedPromoColumn {
        Text(
            text = stringResource(R.string.notification_onboarding_coupon_title),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.notification_onboarding_coupon_body),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = couponVisible,
            enter = scaleIn(
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                initialScale = 0.82f,
            ) + fadeIn(tween(350)),
        ) {
            CouponCard(
                couponCode = ad.couponCode.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.notification_onboarding_no_coupon),
                modifier = Modifier.graphicsLayer {
                    scaleX = couponScale
                    scaleY = couponScale
                },
            )
        }
    }
}

@Composable
private fun AnimatedPromoColumn(
    content: @Composable ColumnScope.() -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(500, easing = FastOutSlowInEasing),
        ) + fadeIn(tween(400)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content,
        )
    }
}

@Composable
private fun PromoImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUrl.takeIf { it.isNotBlank() },
        contentDescription = stringResource(R.string.notification_onboarding_image),
        placeholder = painterResource(R.drawable.onboarding_shop),
        error = painterResource(R.drawable.onboarding_shop),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
private fun CouponCard(
    couponCode: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var copied by remember(couponCode) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.notification_onboarding_coupon_label),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelLarge,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = couponCode,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = {
                    val clipboardManager =
                        context.getSystemService(ClipboardManager::class.java)
                    clipboardManager?.setPrimaryClip(
                        ClipData.newPlainText("coupon_code", couponCode),
                    )
                    copied = true
                },
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (copied) {
                        stringResource(R.string.notification_onboarding_coupon_copied)
                    } else {
                        stringResource(R.string.notification_onboarding_coupon_copy)
                    },
                )
            }
        }
    }
}

@Composable
private fun PromoLoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.notification_onboarding_loading),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PromoErrorContent(
    message: String,
    onRetry: () -> Unit,
    onHome: () -> Unit,
) {
    PromoFeedbackContent(
        title = stringResource(R.string.notification_onboarding_error_title),
        message = message,
        primaryText = stringResource(R.string.retry),
        secondaryText = stringResource(R.string.home),
        onPrimary = onRetry,
        onSecondary = onHome,
    )
}

@Composable
private fun PromoUnavailableContent(
    onHome: () -> Unit,
) {
    PromoFeedbackContent(
        title = stringResource(R.string.notification_onboarding_unavailable_title),
        message = stringResource(R.string.notification_onboarding_unavailable_body),
        primaryText = stringResource(R.string.home),
        secondaryText = null,
        onPrimary = onHome,
        onSecondary = {},
    )
}

@Composable
private fun PromoFeedbackContent(
    title: String,
    message: String,
    primaryText: String,
    secondaryText: String?,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text(text = primaryText)
        }

        if (secondaryText != null) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text(text = secondaryText)
            }
        }
    }
}
