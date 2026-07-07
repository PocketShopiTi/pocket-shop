package com.iti.pocketshop.core.tutorial

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun Modifier.tutorialTarget(step: Int) = composed {
    this.onGloballyPositioned { coords ->
        TutorialManager.reportTarget(step, coords.boundsInWindow())
    }
}

@Composable
fun TutorialOverlay(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onFinish: () -> Unit
) {
    val currentStep by TutorialManager.currentStep.collectAsState()
    val targetBounds by TutorialManager.targetBounds.collectAsState()

    if (!isActive || currentStep < 0) return

    val targetRect = targetBounds[currentStep]

    val alpha by animateFloatAsState(
        targetValue = if (targetRect != null) 1f else 0f,
        animationSpec = tween(300),
        label = "overlayAlpha"
    )

    val instructionText = when (currentStep) {
        0 -> "Search for products here"
        1 -> "Click a product to see details"
        2 -> "Add the product to your wishlist"
        3 -> "Add the product to your cart"
        4 -> "View your saved favorites here"
        5 -> "Check out your cart here"
        6 -> "Manage your profile and orders here"
        else -> ""
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(currentStep) {
                detectTapGestures(
                    onTap = {
                        if (targetRect != null) {
                            if (currentStep >= 6) {
                                onFinish()
                                TutorialManager.endTutorial()
                            } else {
                                TutorialManager.nextStep()
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            drawRect(
                color = Color.Black.copy(alpha = 0.8f * alpha),
                size = size
            )

            if (targetRect != null) {
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = targetRect.topLeft,
                    size = targetRect.size,
                    cornerRadius = CornerRadius(24f, 24f),
                    blendMode = BlendMode.Clear
                )
            }
        }
        
        if (targetRect != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                val isTargetNearTop = targetRect.top < 1000f // Arbitrary point to decide text position
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = instructionText,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap anywhere to continue",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
