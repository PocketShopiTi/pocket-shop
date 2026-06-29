package com.iti.pocketshop.features.profile.presentation

sealed interface ProfileAction {
    data object Retry : ProfileAction
    data object LogoutRequested : ProfileAction
    data object LogoutDismissed : ProfileAction
    data object LogoutConfirmed : ProfileAction
}