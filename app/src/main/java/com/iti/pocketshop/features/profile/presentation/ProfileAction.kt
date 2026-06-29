package com.iti.pocketshop.features.profile.presentation

sealed interface ProfileAction {
    object Retry : ProfileAction
}