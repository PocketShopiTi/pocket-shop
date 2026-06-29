package com.iti.pocketshop.features.splash.presention

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.features.splash.presention.components.SplashAppIcon
import com.iti.pocketshop.features.splash.presention.components.SplashSubtitle
import com.iti.pocketshop.features.splash.presention.components.SplashTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashRoot(
    showNextScreen: () -> Unit
) {

    val scope = rememberCoroutineScope()

    SplashScreen {
        scope.launch {
            delay(1_000L.milliseconds)
            showNextScreen()
        }
    }
}

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    val iconScale = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleSlide = remember { Animatable(40f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleSlide = remember { Animatable(30f) }

    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = EaseOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )

    LaunchedEffect(Unit) {
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = EaseOutExpo)
        )
        titleSlide.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 450, easing = EaseOutExpo)
        )

        delay(80.milliseconds)
        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = EaseOutExpo)
        )
        subtitleSlide.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 400, easing = EaseOutExpo)
        )

        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SplashAppIcon(
                modifier = Modifier
                    .scale(iconScale.value * idlePulse)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SplashTitle(
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .graphicsLayer { translationY = titleSlide.value }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SplashSubtitle(
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .graphicsLayer { translationY = subtitleSlide.value }
            )
        }
    }
}
