package com.iti.pocketshop.features.profile.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.core.components.ObserveEvent
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.presentation.components.GuestProfileScreen
import com.iti.pocketshop.features.profile.presentation.components.LoggedInProfileScreen
import com.iti.pocketshop.features.profile.presentation.components.ProfileLoadingContent
import com.iti.pocketshop.ui.theme.PocketShopTheme

@Composable
fun ProfileRoot(
    openLogin: () -> Unit,
    openRegister: () -> Unit,
    openOrders: () -> Unit = {},
    openSettings: () -> Unit,
    openAddresses: () -> Unit = {},
    openWishList: () -> Unit = {},
    openOrderDetails: (String) -> Unit = {},
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
        openOrderDetails = openOrderDetails,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    openOrderDetails: (String) -> Unit = {},
) {

    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = state.isRefreshing && state.profile != null,
        onRefresh = { onAction(ProfileAction.Refresh) },
        modifier = Modifier.fillMaxSize(),
        indicator = {
            LoadingIndicator(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing && state.profile != null,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        },
    ) {
        when (val profile = state.profile) {
            is ProfileData.Authenticated -> LoggedInProfileScreen(
                profile = profile,
                state = state,
                onReload = { onAction(ProfileAction.Refresh) },
                onLogoutRequested = { onAction(ProfileAction.LogoutRequested) },
                onLogoutConfirmed = { onAction(ProfileAction.LogoutConfirmed) },
                onLogoutDismissed = { onAction(ProfileAction.LogoutDismissed) },
                openOrders = openOrders,
                openAddresses = openAddresses,
                openWishList = openWishList,
                openSettings = openSettings,
                openOrderDetails = openOrderDetails,
            )

            ProfileData.Guest -> GuestProfileScreen(
                openLogin = openLogin,
                openRegister = openRegister,
                openSettings = openSettings,
            )

            null -> if (state.isRefreshing) {
                ProfileLoadingContent()
            } else {
                GuestProfileScreen(
                    openLogin = openLogin,
                    openRegister = openRegister,
                    openSettings = openSettings,
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoadingProfilePreview() {
    PocketShopTheme {
        ProfileScreen(
            state = ProfileState(isRefreshing = true),
            onAction = {},
            openLogin = {},
            openRegister = {},
            openSettings = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun GuestProfilePreview() {
    PocketShopTheme {
        ProfileScreen(
            state = ProfileState(isRefreshing = false, profile = ProfileData.Guest),
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
                isRefreshing = false,
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
                            name = "PK-2026-0847",
                            status = OrderStatus.FULFILLED,
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
