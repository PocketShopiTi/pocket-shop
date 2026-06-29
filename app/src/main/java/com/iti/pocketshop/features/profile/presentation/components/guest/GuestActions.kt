package com.iti.pocketshop.features.profile.presentation.components.guest

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.profile.presentation.components.ProfileMenuCard
import com.iti.pocketshop.features.profile.presentation.components.ProfileMenuRow
import com.iti.pocketshop.features.profile.presentation.components.ProfileSectionLabel


@Composable
fun GuestActions(openSettings: () -> Unit) {
    ProfileSectionLabel(
        text = stringResource(R.string.profile_general),
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
    )
    ProfileMenuCard {
        ProfileMenuRow(
            label = stringResource(R.string.profile_settings),
            icon = Icons.Outlined.Settings,
            onClick = openSettings,
        )
    }
}


