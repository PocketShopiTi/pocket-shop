package com.iti.pocketshop.nestednavigation

import com.iti.pocketshop.R
import com.iti.pocketshop.rootnavigation.Route

enum class BottomBarDestination(
    val title: Int,
    val icon: Int,
    val selectedIcon: Int,
    val route: Route
) {
    Home(
        title = R.string.home,
        icon = R.drawable.ic_home,
        selectedIcon = R.drawable.ic_home_filled,
        route = Route.NestedNav.Home
    ),
    Wishlist(
        title = R.string.wishlist,
        icon = R.drawable.ic_favorites,
        selectedIcon = R.drawable.ic_favorites_filled,
        route = Route.NestedNav.Wishlist
    ),
    Cart(
        title = R.string.cart,
        icon = R.drawable.ic_cart,
        selectedIcon = R.drawable.ic_cart_filled,
        route = Route.NestedNav.Cart
    ),
    Profile(
        title = R.string.profile,
        icon = R.drawable.ic_profile,
        selectedIcon = R.drawable.ic_profile_filled,
        route = Route.NestedNav.Profile
    )
}