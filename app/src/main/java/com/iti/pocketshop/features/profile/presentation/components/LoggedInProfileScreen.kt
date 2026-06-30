package com.iti.pocketshop.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R
import com.iti.pocketshop.core.components.ConfirmationDialog
import com.iti.pocketshop.core.components.ErrorDialog
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.presentation.ProfileState
import com.iti.pocketshop.features.profile.presentation.components.loggedin.ProfileActions
import com.iti.pocketshop.features.profile.presentation.components.loggedin.ProfileHeader
import com.iti.pocketshop.features.profile.presentation.components.loggedin.ProfileStats
import com.iti.pocketshop.features.profile.presentation.components.loggedin.RecentOrders
import com.iti.pocketshop.features.profile.presentation.components.loggedin.RecentOrdersHeader

@Composable
fun LoggedInProfileScreen(
    profile: ProfileData.Authenticated,
    state: ProfileState,
    onReload: () -> Unit,
    onLogoutRequested: () -> Unit,
    onLogoutConfirmed: () -> Unit,
    onLogoutDismissed: () -> Unit,
    openOrders: () -> Unit,
    openAddresses: () -> Unit,
    openWishList: () -> Unit,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            ProfileHeader(
                user = profile.user,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
        state.error?.let { error ->
            item {
                ProfileErrorCard(
                    message = error.toUserMessage(context),
                    onReload = onReload,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        }
        if (profile.stats != null) item {
            ProfileStats(
                orders = profile.stats.ordersCount,
                wishlist = profile.stats.wishListCount,
                addresses = profile.stats.addressesCount,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        if (profile.recentOrders.isNotEmpty()) {
            if (profile.recentOrders.size > 3) {
                item { RecentOrdersHeader(openOrders) }
            }
            item { RecentOrders(profile) }
        }

        item {
            ProfileActions(
                openOrders,
                openAddresses,
                openWishList,
                profile,
                openSettings,
                onLogoutRequested
            )
        }
    }

    if (state.showLogoutConfirmation && state.logoutError == null) {
        ConfirmationDialog(
            title = stringResource(R.string.profile_logout_title),
            message = stringResource(R.string.profile_logout_message),
            confirmText = if (state.isLoggingOut) {
                stringResource(R.string.profile_logging_out)
            } else {
                stringResource(R.string.profile_log_out)
            },
            dismissText = stringResource(R.string.profile_cancel),
            onConfirm = onLogoutConfirmed,
            onDismiss = onLogoutDismissed,
        )
    }

    state.logoutError?.let { error ->
        ErrorDialog(
            message = error.toUserMessage(context),
            onDismiss = onLogoutDismissed,
        )
    }
}
