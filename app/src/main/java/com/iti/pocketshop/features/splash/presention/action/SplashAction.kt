package com.iti.pocketshop.features.splash.presention.action

sealed interface SplashAction {
    data object NavigateToLogin : SplashAction
}