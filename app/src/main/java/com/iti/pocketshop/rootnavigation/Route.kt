package com.iti.pocketshop.rootnavigation

import androidx.navigation3.runtime.NavKey
import com.iti.pocketshop.features.productlist.presentation.ProductListRouteInfo
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {

    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data class OnboardingNotification(val adId: String) : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object EmailVerification : Route

    @Serializable
    data object ForgotPassword : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object NestedNav : Route {

        @Serializable
        data object Home : Route

        @Serializable
        data object Wishlist : Route

        @Serializable
        data object Cart : Route

        @Serializable
        data object Profile : Route

        @Serializable
        data object Orders : Route

    }

    @Serializable
    data class ProductDetails(val id: String) : Route

    @Serializable
    data object AiChat : Route

    @Serializable
    data object OrderCheckout : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object SearchNav : Route {
        @Serializable
        data object Search : Route

        @Serializable
        data object Filters : Route
    }

    @Serializable
    data object Address : Route

    @Serializable
    data object Brands : Route

    @Serializable
    data class ProductList(
        val routeInfo: ProductListRouteInfo
    ) : Route
}
