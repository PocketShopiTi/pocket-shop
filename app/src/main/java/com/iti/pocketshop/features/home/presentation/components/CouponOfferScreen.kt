package com.iti.pocketshop.features.home.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CouponOfferScreen(
    ad: PromotionAd,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)

    val context = LocalContext.current
    val clipLabel = stringResource(R.string.home_coupon_clip_label)

    var contentVisible by remember(ad.id) { mutableStateOf(false) }
    var copied by remember(ad.id) { mutableStateOf(false) }
    val codeScale by animateFloatAsState(
        targetValue = if (copied) 1.04f else 1f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "couponCodeScale",
    )

    LaunchedEffect(ad.id) {
        contentVisible = true
    }

    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_FEEDBACK_DELAY_MILLIS.milliseconds)
            copied = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ad.imageUrl,
                contentDescription = ad.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f),
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.home_coupon_close),
                )
            }

            AnimatedVisibility(
                visible = contentVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(520, easing = FastOutSlowInEasing),
                ) + fadeIn(tween(320)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 28.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(14.dp)
                                .size(32.dp),
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = ad.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    )

                    if (ad.description.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = ad.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    CouponCodeCard(
                        code = ad.couponCode,
                        copied = copied,
                        modifier = Modifier.graphicsLayer {
                            scaleX = codeScale
                            scaleY = codeScale
                        },
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            context.copyCouponToClipboard(
                                clipLabel = clipLabel,
                                couponCode = ad.couponCode,
                            )
                            copied = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.home_coupon_copy_code),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponCodeCard(
    code: String,
    copied: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (copied) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = RoundedCornerShape(18.dp),
            ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_coupon_your_code),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = code,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun Context.copyCouponToClipboard(
    clipLabel: String,
    couponCode: String,
) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(clipLabel, couponCode))
}

private const val COPIED_FEEDBACK_DELAY_MILLIS = 900L
