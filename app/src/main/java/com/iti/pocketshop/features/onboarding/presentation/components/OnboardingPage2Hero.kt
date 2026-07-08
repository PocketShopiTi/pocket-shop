package com.iti.pocketshop.features.onboarding.presentation.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors


@Composable
fun OnboardingPage2Hero(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    val anim = rememberOnboardingAnimState(itemCount = 5, staggerMs = 120L, visible = visible)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clipToBounds(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .graphicsLayer(anim.layerAt(0))
                    .background(
                        color = colors.primary,
                        shape = RoundedCornerShape(
                            topStart = 18.dp, topEnd = 18.dp,
                            bottomStart = 18.dp, bottomEnd = 4.dp,
                        ),
                    )
                    .padding(horizontal = 18.dp, vertical = 11.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_ai_prompt),
                    color = colors.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.graphicsLayer(anim.layerAt(1)),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(colors.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image10),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = colors.surface,
                            shape = RoundedCornerShape(
                                topStart = 18.dp, topEnd = 18.dp,
                                bottomStart = 4.dp, bottomEnd = 18.dp,
                            ),
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_ai_response),
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ProductSuggestionCard(
                    imageRes = R.drawable.image8,
                    title = stringResource(R.string.onboarding_product3_title),
                    price = stringResource(R.string.onboarding_product3_price),
                    scale = anim.scales[2].value,
                    alpha = anim.alphas[2].value,
                    translationY = anim.offsetYs[2].value,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
                ProductSuggestionCard(
                    imageRes = R.drawable.image1,
                    title = stringResource(R.string.onboarding_product4_title),
                    price = stringResource(R.string.onboarding_product4_price),
                    scale = anim.scales[3].value,
                    alpha = anim.alphas[3].value,
                    translationY = anim.offsetYs[3].value,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
                ProductSuggestionCard(
                    imageRes = R.drawable.image3,
                    title = stringResource(R.string.onboarding_product5_title),
                    price = stringResource(R.string.onboarding_product5_price),
                    scale = anim.scales[4].value,
                    alpha = anim.alphas[4].value,
                    translationY = anim.offsetYs[4].value,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
