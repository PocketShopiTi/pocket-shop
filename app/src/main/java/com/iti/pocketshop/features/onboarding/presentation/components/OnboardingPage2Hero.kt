package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun OnboardingPage2Hero(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val initialOffsetPx = with(density) { 40.dp.toPx() }

    val springSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val scales = remember { List(5) { Animatable(0.7f) } }
    val alphas = remember { List(5) { Animatable(0f) } }
    val offsetYs = remember { List(5) { Animatable(initialOffsetPx) } }

    LaunchedEffect(Unit) {
        val staggerDelays = listOf(0L, 120L, 240L, 360L, 480L)
        staggerDelays.indices.forEach { i ->
            launch {
                delay(staggerDelays[i].milliseconds)
                launch { scales[i].animateTo(1f, springSpec) }
                launch { alphas[i].animateTo(1f, tween(500)) }
                launch { offsetYs[i].animateTo(0f, springSpec) }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clipToBounds()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .graphicsLayer {
                        scaleX = scales[0].value
                        scaleY = scales[0].value
                        alpha = alphas[0].value
                        translationY = offsetYs[0].value
                    }
                    .background(
                        color = Color(0xFFB5673A),
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 11.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_ai_prompt),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.graphicsLayer {
                    scaleX = scales[1].value
                    scaleY = scales[1].value
                    alpha = alphas[1].value
                    translationY = offsetYs[1].value
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF2E4D8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image10),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 18.dp
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_ai_response),
                        color = Color(0xFF1A1A1A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProductSuggestionCard(
                    imageRes = R.drawable.image8,
                    title = stringResource(R.string.onboarding_product3_title),
                    price = stringResource(R.string.onboarding_product3_price),
                    scale = scales[2].value,
                    alpha = alphas[2].value,
                    translationY = offsetYs[2].value,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                ProductSuggestionCard(
                    imageRes = R.drawable.image1,
                    title = stringResource(R.string.onboarding_product4_title),
                    price = stringResource(R.string.onboarding_product4_price),
                    scale = scales[3].value,
                    alpha = alphas[3].value,
                    translationY = offsetYs[3].value,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                ProductSuggestionCard(
                    imageRes = R.drawable.image3,
                    title = stringResource(R.string.onboarding_product5_title),
                    price = stringResource(R.string.onboarding_product5_price),
                    scale = scales[4].value,
                    alpha = alphas[4].value,
                    translationY = offsetYs[4].value,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

    }
}