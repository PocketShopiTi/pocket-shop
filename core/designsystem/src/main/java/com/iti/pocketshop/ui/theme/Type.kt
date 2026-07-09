package com.iti.pocketshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.iti.pocketshop.core.designsystem.R

// Serif display font used for headings, names, stat values and prices.
val FrauncesFontFamily = FontFamily(
    Font(
        resId = R.font.fraunces_light,
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.fraunces,
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.fraunces_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resId = R.font.fraunces_bold,
        weight = FontWeight.Bold,
    ),
)

// Sans body font used for labels, descriptions and supporting text.
val PlusJakartaSansFontFamily = FontFamily(
    Font(
        resId = R.font.plus_jakarta_sans_light,
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.plus_jakarta_sans_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resId = R.font.plus_jakarta_sans_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resId = R.font.plus_jakarta_sans_bold,
        weight = FontWeight.Bold,
    ),
)

private val baseline = Typography()


val Typography = Typography(
    // Display / headline / title -> Fraunces (serif display)
    displayLarge = baseline.displayLarge.copy(fontFamily = FrauncesFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = FrauncesFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = FrauncesFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = FrauncesFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = FrauncesFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = FrauncesFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = FrauncesFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = FrauncesFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = FrauncesFontFamily),
    // Body / label -> Plus Jakarta Sans (sans body)
    bodyLarge = baseline.bodyLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = PlusJakartaSansFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = PlusJakartaSansFontFamily),
)
