package com.iti.pocketshop.nestednavigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.iti.pocketshop.LocalUser
import com.iti.pocketshop.core.components.SignInDialogController
import com.iti.pocketshop.features.cart.CartRoot
import com.iti.pocketshop.features.home.presentation.HomeRoot
import com.iti.pocketshop.features.orders.presentation.OrdersRoot
import com.iti.pocketshop.features.productlist.presentation.ProductListRouteInfo
import com.iti.pocketshop.features.profile.presentation.ProfileRoot
import com.iti.pocketshop.features.wishlist.presentation.screen.WishlistRoot
import com.iti.pocketshop.rootnavigation.Route
import com.iti.pocketshop.rootnavigation.navigateSingleTop
import kotlinx.coroutines.launch

@Composable
fun NestedNavDisplay(
    currentRootRoute: NavKey?,
    navigateBack: () -> Unit,
    openSearch: () -> Unit,
    openBrands: () -> Unit,
    openProductList: (ProductListRouteInfo) -> Unit,
    logout: () -> Unit,
    openProductDetails: (String) -> Unit,
    openSettings: () -> Unit,
    openAddresses: () -> Unit,
    openLogin: () -> Unit,
    openRegister: () -> Unit,
    openOrderCheckout: () -> Unit,
    openAiChat: () -> Unit,
) {

    val nestedBackStack = rememberNavBackStack(Route.NestedNav.Home)

    val currentUser = LocalUser.current
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomBarDestination.entries.forEachIndexed { index, destination  ->
                    val currentRoute = nestedBackStack.lastOrNull()
                    val isSelected = currentRoute == destination.route ||
                            destination == BottomBarDestination.Profile &&
                            currentRoute == Route.NestedNav.Orders
                    BottomNavigationButton(
                        onClick = {
                            if (currentUser?.isAnonymous == true && (
                                        destination.route == Route.NestedNav.Wishlist ||
                                                destination.route == Route.NestedNav.Cart
                                        )
                            ) {
                                scope.launch {
                                    SignInDialogController.sendEvent(true)
                                }
                                return@BottomNavigationButton
                            }
                            nestedBackStack.apply {
                                clear()
                                if (destination.route != Route.NestedNav.Home) {
                                    navigateSingleTop(Route.NestedNav.Home)
                                }
                                navigateSingleTop(destination.route)
                            }
                        },
                        icon = if (isSelected) destination.selectedIcon else destination.icon,
                        modifier = Modifier
                            .weight(1f),
                        selected = isSelected,
                        label = destination.title
                    )
                    if (index == 1) {
                        AiChatFab(onClick = openAiChat)
                    }
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            backStack = nestedBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            onBack = {
                if (currentRootRoute == Route.NestedNav) {
                    nestedBackStack.removeLastOrNull()
                } else {
                    navigateBack()
                }
            },
            transitionSpec = {
                fadeIn(tween(350)) togetherWith fadeOut(tween(350))
            },
            entryProvider = entryProvider {
                entry<Route.NestedNav.Home> {
                    HomeRoot(
                        openProductDetails = openProductDetails,
                        openSearch = openSearch,
                        openBrands = openBrands,
                        openProductList = openProductList,
                    )
                }
                entry<Route.NestedNav.Wishlist> {
                    WishlistRoot(
                        openProductDetails = openProductDetails,
                    )
                }
                entry<Route.NestedNav.Cart> {
                    CartRoot(
                        onStartShoppingClick = {
                            nestedBackStack.navigateSingleTop(Route.NestedNav.Home)
                        },
                        onCheckoutClick = {
                            openOrderCheckout()
                        }
                    )
                }
                entry<Route.NestedNav.Profile> {
                    ProfileRoot(
                        openLogin = openLogin,
                        openRegister = openRegister,
                        openSettings = openSettings,
                        openAddresses = openAddresses,
                        openWishList = {
                            nestedBackStack.navigateSingleTop(Route.NestedNav.Wishlist)
                        },
                        openOrders = {
                            nestedBackStack.navigateSingleTop(Route.NestedNav.Orders)
                        },
                        logout = logout,
                    )
                }
                entry<Route.NestedNav.Orders> {
                    OrdersRoot(
                        onBack = { nestedBackStack.removeLastOrNull() },
                        onTrack = {},
                        onView = {},
                    )
                }
            }
        )
    }
}
