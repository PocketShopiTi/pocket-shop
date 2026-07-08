package com.iti.pocketshop.features.splash.presention

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iti.pocketshop.LocalSettingsUser
import com.iti.pocketshop.features.splash.presention.components.SplashAppIcon
import com.iti.pocketshop.features.splash.presention.components.SplashSubtitle
import com.iti.pocketshop.features.splash.presention.components.SplashTitle
import com.iti.pocketshop.rootnavigation.Route
import com.iti.pocketshop.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashRoot(
    showNextScreen: (Route) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val settings = LocalSettingsUser.current
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            showNextScreen(
                when (event) {
                    SplashEvent.OpenLogin -> Route.Login
                    SplashEvent.OpenOnboarding -> Route.Onboarding
                    SplashEvent.OpenHome -> Route.NestedNav
                    SplashEvent.OpenVerification -> Route.EmailVerification
                }
            )
        }
    }
    SplashScreen(
        onFinished = {
            viewModel.resolveSession(settings.hasSeenOnboarding)
        }
    )
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val colors = LocalExtendedColors.current

    val iconScale = remember { Animatable(0.6f) }
    val iconAlpha = remember { Animatable(0f) }
    val iconRotation = remember { Animatable(-6f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleSlide = remember { Animatable(24f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleSlide = remember { Animatable(16f) }
    val loadingAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "iconPulse",
    )

    LaunchedEffect(Unit) {
        launch { iconAlpha.animateTo(1f, tween(400, easing = EaseOutCubic)) }
        launch {
            iconRotation.animateTo(
                0f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            )
        }
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        )
        launch { titleSlide.animateTo(0f, tween(400, easing = EaseOutCubic)) }
        titleAlpha.animateTo(1f, tween(400, easing = EaseOutCubic))
        delay(80.milliseconds)
        launch { subtitleSlide.animateTo(0f, tween(350, easing = EaseOutCubic)) }
        subtitleAlpha.animateTo(1f, tween(350, easing = EaseOutCubic))
        loadingAlpha.animateTo(1f, tween(300))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SplashAppIcon(
                scale = iconScale.value * idlePulse,
                alpha = iconAlpha.value,
                rotation = iconRotation.value,
            )
            Spacer(modifier = Modifier.height(24.dp))
            SplashTitle(
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .graphicsLayer { translationY = titleSlide.value },
            )
            Spacer(modifier = Modifier.height(8.dp))
            SplashSubtitle(
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .graphicsLayer { translationY = subtitleSlide.value },
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}