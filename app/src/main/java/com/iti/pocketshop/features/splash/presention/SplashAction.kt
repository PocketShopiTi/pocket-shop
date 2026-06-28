package com.iti.pocketshop.features.splash.presention

sealed interface SplashAction {
    data object NavigateToLogin : SplashAction
}