package com.iti.pocketshop.features.onboarding.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors


@Composable
fun OnboardingActionButtons(
    isLastPage: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalExtendedColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "buttonScale",
    )

    Button(
        onClick = onNext,
        interactionSource = interactionSource,
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isLastPage) Color.Transparent else colors.primary,
                shape = CircleShape,
            )
            .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isLastPage) colors.primary else Color.Transparent,
            contentColor = if (isLastPage) colors.onPrimary else colors.primary,
        ),
    ) {
        Text(
            text = stringResource(
                if (isLastPage) R.string.onboarding_get_started
                else R.string.onboarding_next
            ),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            fontSize = 20.sp
        )
    }
}
