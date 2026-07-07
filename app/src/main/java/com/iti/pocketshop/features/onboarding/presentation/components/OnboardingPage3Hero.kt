package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.iti.pocketshop.R

@Composable
fun OnboardingPage3Hero(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val anim = rememberOnboardingAnimState(itemCount = 3, staggerMs = 180L, visible = visible)

    fun animModifier(i: Int) = Modifier.graphicsLayer {
        scaleX = anim.scales[i].value
        scaleY = anim.scales[i].value
        alpha = anim.alphas[i].value
        translationY = anim.offsetYs[i].value
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .padding(top = 8.dp)
            .clipToBounds(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OnboardingItemCard(
                modifier = animModifier(0),
                imageRes = R.drawable.image4,
                title = stringResource(R.string.onboarding_product1_title),
                subtitle = stringResource(R.string.onboarding_product1_price),
                subtitleColor = Color(0xFFB5673A),
                trailing = OnboardingCardTrailing.FavouriteIcon(
                    iconRes = R.drawable.ic_favorites_filled,
                    tint = Color(0xFFB5673A),
                ),
            )

            OnboardingItemCard(
                modifier = animModifier(1),
                imageRes = R.drawable.image6,
                title = stringResource(R.string.onboarding_product2_title),
                subtitle = stringResource(R.string.onboarding_product2_price),
                trailing = OnboardingCardTrailing.QuantityStepper(quantity = 2),
            )

            OnboardingItemCard(
                modifier = animModifier(2),
                iconRes = R.drawable.onboarding_checkout,
                iconTint = Color(0xFF2E9E6B),
                iconBgColor = Color(0xFFE8F5F0),
                iconBgShape = CircleShape,
                title = stringResource(R.string.onboarding_order_confirmed),
                subtitle = stringResource(R.string.onboarding_order_id),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(3f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                        startY = 500f,
                        endY = 900f,
                    )
                )
        )
    }
}