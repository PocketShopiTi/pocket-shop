package com.iti.pocketshop.features.splash.presention.view.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.iti.pocketshop.R

@Composable
fun SplashSubtitle(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.splash_subtitle),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
