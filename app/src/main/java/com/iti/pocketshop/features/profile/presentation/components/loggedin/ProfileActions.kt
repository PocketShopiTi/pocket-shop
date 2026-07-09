package com.iti.pocketshop.features.profile.presentation.components.loggedin

import androidx.compose.foundation.layout.padding
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
            icon = R.drawable.box,
            onClick = openOrders,
//            addedInfo = profile.stats?.ordersCount
        )
        ProfileMenuRow(
            label = stringResource(R.string.profile_addresses),
            icon = R.drawable.location,
            onClick = openAddresses,
//            addedInfo = profile.stats?.addressesCount
        )
        ProfileMenuRow(
            label = stringResource(R.string.wishlist),
            icon = R.drawable.favorite,
            onClick = openWishList,
//            addedInfo = profile.stats?.wishListCount
        )
        ProfileMenuRow(
            label = stringResource(R.string.profile_settings),
            icon = R.drawable.ic_settings_filled,
            onClick = openSettings,
        )
        ProfileMenuRow(
            label = stringResource(R.string.profile_log_out),
            icon = R.drawable.logout,
            onClick = onLogoutRequested,
            isDestructive = true,
            showDivider = false,
        )
    }
}
