package com.iti.pocketshop.features.auth.login.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R

@Composable
fun LoginLogo(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.app_logo),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier.size(112.dp)
    )
}

@Preview
@Composable
private fun LoginLogoPreview() {
    LoginLogo()
}