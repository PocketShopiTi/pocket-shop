package com.iti.pocketshop.features.splash.presention.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R
import com.iti.pocketshop.ui.theme.LocalExtendedColors

@Composable
fun SplashSubtitle(modifier: Modifier = Modifier) {
    val colors = LocalExtendedColors.current
    Text(
        modifier = modifier,
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            color = colors.textSecondary,
        ),
    )
}
