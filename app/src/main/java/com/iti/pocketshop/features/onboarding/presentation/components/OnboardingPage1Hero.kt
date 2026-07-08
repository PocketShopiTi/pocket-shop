package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors


@Composable
fun OnboardingPage1Hero(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    val anim = rememberOnboardingAnimState(
        itemCount = 6,
        staggerMs = 120L,
        initialScale = 0.7f,
        visible = visible,
    )

    val tagModifier = Modifier.zIndex(2f).graphicsLayer(anim.layerAt(5))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp),
        contentAlignment = Alignment.Center,
    ) {
        RotatedImageCard(
            imageRes = R.drawable.image11,
            width = 130,
            height = 180,
            offsetX = -95,
            offsetY = -60,
            rotation = -8f,
            scale = anim.scales[1].value,
            alpha = anim.alphas[1].value,
            translationY = anim.offsetYs[1].value,
        )

        RotatedImageCard(
            imageRes = R.drawable.image1,
            width = 130,
            height = 190,
            offsetX = 90,
            offsetY = -65,
            rotation = 8f,
            scale = anim.scales[1].value,
            alpha = anim.alphas[1].value,
            translationY = anim.offsetYs[1].value,
        )
        RotatedImageCard(
            imageRes = R.drawable.image2,
            width = 135, height = 135,
            offsetX = -90, offsetY = 80, rotation = 5f,
            scale = anim.scales[2].value, alpha = anim.alphas[2].value, translationY = anim.offsetYs[2].value,
        )
        RotatedImageCard(
            imageRes = R.drawable.image7,
            width = 120, height = 130,
            offsetX = 95, offsetY = 95, rotation = -7f,
            scale = anim.scales[3].value, alpha = anim.alphas[3].value, translationY = anim.offsetYs[3].value,
        )
        RotatedImageCard(
            imageRes = R.drawable.image5,
            width = 130, height = 130,
            offsetX = 5, offsetY = 10, rotation = -12f,
            zIndex = 1f, cornerRadius = 20,
            scale = anim.scales[4].value, alpha = anim.alphas[4].value, translationY = anim.offsetYs[4].value,
        )

        FloatingTag(
            stringResource(R.string.onboarding_category_women),
            Modifier.offset(x = (-35).dp, y = (-25).dp).then(tagModifier)
        )
        FloatingTag(stringResource(R.string.onboarding_category_men), Modifier.offset(x = 45.dp, y = (-14).dp).then(tagModifier))
        FloatingTag(stringResource(R.string.onboarding_category_shoes), Modifier.offset(x = (-60).dp, y = 35.dp).then(tagModifier))
        FloatingTag(stringResource(R.string.onboarding_category_bags), Modifier.offset(x = 5.dp, y = 42.dp).then(tagModifier))
        FloatingTag(stringResource(R.string.onboarding_category_beauty), Modifier.offset(x = 80.dp, y = 32.dp).then(tagModifier))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(3f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, colors.background),
                        startY = 500f,
                        endY = 900f,
                    )
                ),
        )
    }
}
