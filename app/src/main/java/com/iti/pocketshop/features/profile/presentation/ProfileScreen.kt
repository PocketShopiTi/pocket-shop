package com.iti.pocketshop.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.core.components.ObserveEvent
import com.iti.pocketshop.core.networkutils.toUserMessage
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.presentation.components.GuestProfileScreen
import com.iti.pocketshop.features.profile.presentation.components.LoggedInProfileScreen
import com.iti.pocketshop.features.profile.presentation.components.ProfileErrorContent
import com.iti.pocketshop.ui.theme.PocketShopTheme

@Composable
fun ProfileRoot(
    openLogin: () -> Unit,
    openRegister: () -> Unit,
    openOrders: () -> Unit = {},
    openSettings: () -> Unit,
    openAddresses: () -> Unit = {},
    openWishList: () -> Unit = {},
    logout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvent(viewModel.events) { event ->
        when (event) {
            ProfileEvent.LoggedOut -> logout()
        }
    }

    ProfileScreen(
        state = state,
        onAction = { viewModel.onAction(action = it) },
        openLogin = openLogin,
        openRegister = openRegister,
        openSettings = openSettings,
        openOrders = openOrders,
        openAddresses = openAddresses,
        openWishList = openWishList,
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    openLogin: () -> Unit,
    openRegister: () -> Unit,
    openSettings: () -> Unit,
    openOrders: () -> Unit = {},
    openAddresses: () -> Unit = {},
    openWishList: () -> Unit = {},
) {
    val context = LocalContext.current
    when {
        state.error != null -> ProfileErrorContent(
            message = state.error.toUserMessage(context),
            onRetry = { onAction(ProfileAction.Retry) },
        )

        state.profile is ProfileData.Authenticated -> LoggedInProfileScreen(
            profile = state.profile,
            state = state,
            onLogoutRequested = { onAction(ProfileAction.LogoutRequested) },
            onLogoutConfirmed = { onAction(ProfileAction.LogoutConfirmed) },
            onLogoutDismissed = { onAction(ProfileAction.LogoutDismissed) },
            openOrders = openOrders,
            openAddresses = openAddresses,
            openWishList = openWishList,
            openSettings = openSettings,
        )

        else -> GuestProfileScreen(
            openLogin = openLogin,
            openRegister = openRegister,
            openSettings = openSettings,
        )
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun GuestProfilePreview() {
    PocketShopTheme {
        ProfileScreen(
            state = ProfileState(isLoading = false, profile = ProfileData.Guest),
            onAction = {},
            openLogin = {},
            openRegister = {},
            openSettings = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoggedInProfilePreview() {
    PocketShopTheme {
        ProfileScreen(
            state = ProfileState(
                isLoading = false,
                profile = ProfileData.Authenticated(
                    user = UserEntity(
                        id = "preview-user",
                        name = "Sofia Chen",
                        email = "sofia@example.com",
                        imageUrl = null,
                        memberSinceEpochMillis = 1_709_251_200_000,
                    ),
                    stats = ProfileStats(ordersCount = 12, wishListCount = 4, addressesCount = 2),
                    recentOrders = listOf(
                        OrderEntity(
                            id = "PK-2026-0847",
                            status = OrderStatus.DELIVERED,
                            total = 778.50,
                            currencyCode = "USD",
                            imageUrl = null,
                        )
                    ),
                ),
            ),
            onAction = {},
            openLogin = {},
            openRegister = {},
            openSettings = {},
        )
    }
}