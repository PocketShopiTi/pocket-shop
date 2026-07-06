package com.iti.pocketshop.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val outline: Color,
    val success: Color,
    val error: Color,
    val warning: Color,
    val info: Color,
    val snackbarContainer: Color,
    val snackbarContent: Color,
    val snackbarAction: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val lightExtendedColors = ExtendedColors(
    background = Color(0xFFFBF7F0),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF4EFE6),
    textPrimary = Color(0xFF2A2520),
    textSecondary = Color(0xFF8A8378),
    primary = Color(0xFFC0653B),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF6B7256),
    outline = Color(0xFFE6E0D4),
    success = Color(0xFF16A34A),
    error = Color(0xFFDC2626),
    warning = Color(0xFFD97706),
    info = Color(0xFF2563EB),
    snackbarContainer = Color(0xFFF2EBDF),
    snackbarContent = Color(0xFF1A1714),
    snackbarAction = Color(0xFFD97A4E),
)

val darkExtendedColors = ExtendedColors(
    background = Color(0xFF1A1714),
    surface = Color(0xFF24201B),
    surfaceVariant = Color(0xFF2D2823),
    textPrimary = Color(0xFFF2EBDF),
    textSecondary = Color(0xFFA89F90),
    primary = Color(0xFFD97A4E),
    onPrimary = Color(0xFF1A1714),
    secondary = Color(0xFF939B76),
    outline = Color(0xFF3A332B),
    success = Color(0xFF4ADE80),
    error = Color(0xFFF87171),
    warning = Color(0xFFFBBF24),
    info = Color(0xFF60A5FA),
    snackbarContainer = Color(0xFF2A2520),
    snackbarContent = Color(0xFFFBF7F0),
    snackbarAction = Color(0xFFF9BB72),

)