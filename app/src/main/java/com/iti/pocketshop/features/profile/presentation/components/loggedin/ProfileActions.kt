package com.iti.pocketshop.features.profile.presentation.components.loggedin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.presentation.components.ProfileMenuCard
import com.iti.pocketshop.features.profile.presentation.components.ProfileMenuRow


@Composable
fun ProfileActions(
    openOrders: () -> Unit,
    openAddresses: () -> Unit,
    openWishList: () -> Unit,
    profile: ProfileData.Authenticated,
    openSettings: () -> Unit,
    onLogoutRequested: () -> Unit
) {
    ProfileMenuCard(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    )
    {
        ProfileMenuRow(
            label = stringResource(R.string.profile_my_orders),
            icon = Icons.Outlined.Inventory2,
            onClick = openOrders,
        )
        ProfileMenuRow(
            label = stringResource(R.string.profile_addresses),
            icon = Icons.Outlined.LocationOn,
            onClick = openAddresses,
        )
        ProfileMenuRow(
            label = stringResource(R.string.wishlist),
            icon = Icons.Outlined.FavoriteBorder,
            onClick = openWishList,
        ) {
            Text(
                text = stringResource(
                    R.string.profile_saved_count,
                    profile.stats.wishListCount,
                ),
                modifier = Modifier.padding(end = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        ProfileMenuRow(
            label = stringResource(R.string.profile_settings),
            icon = Icons.Outlined.Settings,
            onClick = openSettings,
        )
        ProfileMenuRow(
            label = stringResource(R.string.profile_log_out),
            icon = Icons.AutoMirrored.Outlined.Logout,
            onClick = onLogoutRequested,
            isDestructive = true,
            showDivider = false,
        )
    }
}
