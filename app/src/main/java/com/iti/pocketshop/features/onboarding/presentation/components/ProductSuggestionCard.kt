package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
fun ProductSuggestionCard(
    @DrawableRes imageRes: Int?,
    title: String,
    price: String,
    scale: Float,
    alpha: Float,
    translationY: Float,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    OutlinedCard (
        modifier = modifier.graphicsLayer {
            scaleX = scale; scaleY = scale
            this.alpha = alpha
            this.translationY = translationY
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(colors.surfaceVariant),
            ) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 16.dp),
            ) {
                Text(text = title, style = OnboardingTypography.cardTitle.copy(fontSize = 13.sp, color = colors.textPrimary))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = price, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.textSecondary)
            }
        }
    }
}
