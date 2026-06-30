package com.iti.pocketshop.rootnavigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object OTP : Route

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
    data object Search : Route

    @Serializable
    data object Categories : Route

    @Serializable
    data class ProductList(val type: String) : Route
}