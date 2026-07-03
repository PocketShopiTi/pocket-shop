package com.iti.pocketshop.features.profile.presentation

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
}
