package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


data class OnboardingAnimValues(
    val scales: List<Animatable<Float, *>>,
    val alphas: List<Animatable<Float, *>>,
    val offsetYs: List<Animatable<Float, *>>,
)

@Composable
fun rememberOnboardingAnimState(
    itemCount: Int,
    staggerMs: Long = 150L,
    initialScale: Float = 0.8f,
): OnboardingAnimValues {
    val density = LocalDensity.current
    val initialOffsetPx = with(density) { 40.dp.toPx() }

    val springSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness    = Spring.StiffnessLow,
    )

    val scales   = remember { List(itemCount) { Animatable(initialScale) } }
    val alphas   = remember { List(itemCount) { Animatable(0f) } }
    val offsetYs = remember { List(itemCount) { Animatable(initialOffsetPx) } }

    LaunchedEffect(Unit) {
        scales.indices.forEach { i ->
            launch {
                delay((i * staggerMs).milliseconds)
                launch { scales[i].animateTo(1f, springSpec) }
                launch { alphas[i].animateTo(1f, tween(500)) }
                launch { offsetYs[i].animateTo(0f, springSpec) }
            }
        }
    }

    return OnboardingAnimValues(scales, alphas, offsetYs)
}
