package com.iti.pocketshop.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


@Immutable
data class ExtendedColors(
    val primaryOrange: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val lightExtendedColors = ExtendedColors(
    primaryOrange = Color(0xFFF57F17)
)

val darkExtendedColors = ExtendedColors(
    primaryOrange = Color(0xFFFFB74D)
)