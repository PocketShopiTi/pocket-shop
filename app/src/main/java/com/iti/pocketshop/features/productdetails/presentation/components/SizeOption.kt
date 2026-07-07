package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R


@Composable
fun SizeOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val unavailable = stringResource(R.string.product_details_option_unavailable, label)
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface,
        label = "optionContainerColor",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "optionBorderColor",
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        label = "optionScale",
    )
    Box(
        modifier = Modifier
            .height(44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics { if (!enabled) contentDescription = unavailable }
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(20.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.35f),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
        )
    }
}

@Composable
fun GenericOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) = SizeOption(
    label = label,
    selected = selected,
    enabled = enabled,
    onClick = onClick,
)
