package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(top = 16.dp),
        color = MaterialTheme.colorScheme.outline,
    )
}

@Composable
internal fun sectionLabelStyle(): TextStyle = MaterialTheme.typography.labelSmall.copy(
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.88.sp,
    fontWeight = FontWeight.Medium,
)
