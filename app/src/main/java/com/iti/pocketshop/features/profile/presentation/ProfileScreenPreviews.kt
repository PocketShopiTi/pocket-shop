package com.iti.pocketshop.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.model.OrderStatus
import com.iti.pocketshop.features.profile.domain.model.ProfileData
import com.iti.pocketshop.features.profile.domain.model.ProfileStats
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.ui.theme.PocketShopTheme


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
