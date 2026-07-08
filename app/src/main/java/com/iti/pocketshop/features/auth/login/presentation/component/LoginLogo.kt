package com.iti.pocketshop.features.auth.login.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.iti.pocketshop.R

@Composable
fun LoginLogo(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.app_logo),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier,
    )
}
