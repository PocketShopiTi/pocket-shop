package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
fun OnboardingItemCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int? = null,
    @DrawableRes iconRes: Int? = null,
    iconTint: Color = LocalExtendedColors.current.primary,
    iconBgColor: Color = LocalExtendedColors.current.surfaceVariant,
    iconBgShape: Shape = RoundedCornerShape(12.dp),
    subtitleColor: Color = LocalExtendedColors.current.textSecondary,
    trailing: OnboardingCardTrailing = OnboardingCardTrailing.None,
) {
    val colors = LocalExtendedColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(iconBgShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center,
        ) {
            when {
                imageRes != null -> Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                iconRes != null -> Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = OnboardingTypography.cardTitle.copy(color = colors.textPrimary),
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = subtitleColor,
            )
        }

        when (val t = trailing) {
            is OnboardingCardTrailing.FavouriteIcon -> Icon(
                painter = painterResource(id = t.iconRes),
                contentDescription = null,
                tint = t.tint,
                modifier = Modifier.size(20.dp),
            )
            is OnboardingCardTrailing.QuantityStepper -> QuantityControl(
                qty = t.quantity
            )
            OnboardingCardTrailing.None -> Unit
        }
    }
}


sealed class OnboardingCardTrailing {
    data class FavouriteIcon(@param:DrawableRes val iconRes: Int, val tint: Color) : OnboardingCardTrailing()
    data class QuantityStepper(val quantity: Int) : OnboardingCardTrailing()
    object None : OnboardingCardTrailing()
}


object OnboardingTypography {
    val cardTitle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun QuantityControl(qty: Int) {
    val colors = LocalExtendedColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text("−", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
        Text("$qty", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primary)
    }
}