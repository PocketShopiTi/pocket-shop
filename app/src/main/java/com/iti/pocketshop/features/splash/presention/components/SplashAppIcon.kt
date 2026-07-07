package com.iti.pocketshop.features.splash.presention.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashAppIcon(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit = {},
) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-8f) }
    val offsetY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 450)
                )
            }

            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 550,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                rotation.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            launch {
                scale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = FastOutSlowInEasing
                    )
                )

                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
        }

        delay(500.milliseconds)
        onAnimationFinished()
    }

    Image(
        painter = painterResource(R.drawable.app_logo),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
                translationY = offsetY.value
            }
            .size(112.dp)
    )
}