package com.iti.pocketshop.features.address.presentation.view.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
internal fun AddressFieldTextField(
    value: String,
    label: String,
    error: String?,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
) {
    val extendedColors = LocalExtendedColors.current
    val errorColor = MaterialTheme.colorScheme.error
    val density = LocalDensity.current
    val shakeOffset = remember { Animatable(0f) }
    val shakeDistance = with(density) { 10.dp.toPx() }

    LaunchedEffect(error) {
        if (error != null) {
            shakeOffset.snapTo(0f)
            listOf(1f, -1f, 0.75f, -0.75f, 0.35f, -0.35f, 0f).forEach { multiplier ->
                shakeOffset.animateTo(
                    targetValue = shakeDistance * multiplier,
                    animationSpec = tween(
                        durationMillis = 40,
                        easing = LinearEasing,
                    ),
                )
            }
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    Column(
        modifier = Modifier.graphicsLayer {
            translationX = shakeOffset.value
        },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label.uppercase(),
                color = if (error != null) errorColor else extendedColors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(16.dp),
            isError = error != null,
            supportingText = error?.let { text ->
                {
                    Text(
                        text = text,
                        color = errorColor,
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = extendedColors.primary,
                unfocusedBorderColor = extendedColors.outline,
                focusedContainerColor = extendedColors.surface,
                unfocusedContainerColor = extendedColors.surface,
                errorBorderColor = errorColor,
                errorContainerColor = extendedColors.surface,
                cursorColor = extendedColors.primary,
                focusedTextColor = extendedColors.textPrimary,
                unfocusedTextColor = extendedColors.textPrimary,
            ),
        )
    }
}
