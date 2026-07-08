package com.iti.pocketshop.features.home.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.home.domain.models.PromotionAd
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CouponOfferScreen(
    ad: PromotionAd,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val colors = LocalExtendedColors.current
    val haptics = LocalHapticFeedback.current
    val clipLabel = stringResource(R.string.home_coupon_clip_label)

    var copied by remember(ad.id) { mutableStateOf(false) }
    val codeScale by animateFloatAsState(
        targetValue = if (copied) 1.04f else 1f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "couponCodeScale",
    )

    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_FEEDBACK_DELAY_MILLIS.milliseconds)
            copied = false
        }
    }

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ad.imageUrl,
                    contentDescription = ad.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.55f to colors.background.copy(alpha = 0.85f),
                                    1f to colors.background,
                                ),
                            )
                        },
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.surface.copy(alpha = 0.9f),
                        contentColor = colors.textPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.home_coupon_close),
                    )
                }

                AnimatedVisibility(
                    visible = contentVisible,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(450, easing = FastOutSlowInEasing),
                    ) + fadeIn(tween(400)),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            shape = CircleShape,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = colors.success,
                                modifier = Modifier
                                    .padding(14.dp)
                                    .size(48.dp),
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        Text(
                            text = ad.title,
                            color = colors.textPrimary,
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
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                copied = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (copied) colors.success else colors.primary,
                                contentColor = colors.onPrimary,
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            AnimatedContent(
                                targetState = copied,
                                transitionSpec = {
                                    (fadeIn(tween(200)) + scaleIn(initialScale = 0.85f))
                                        .togetherWith(fadeOut(tween(150)))
                                },
                                label = "copyButtonContent",
                            ) { isCopied ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isCopied) Icons.Outlined.Check
                                        else Icons.Outlined.ContentCopy,
                                        contentDescription = null,
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    Text(
                                        text = stringResource(
                                            if (isCopied) R.string.home_coupon_copied
                                            else R.string.home_coupon_copy_code
                                        ),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(120.dp))
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
    val colors = LocalExtendedColors.current
    val borderColor by animateColorAsState(
        targetValue = if (copied) colors.success else colors.outline,
        animationSpec = tween(220),
        label = "codeBorderColor",
    )

    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(18.dp)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_coupon_your_code),
                color = colors.textSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = code,
                color = colors.textPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 2.sp,
            )
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