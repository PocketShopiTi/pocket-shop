package com.iti.pocketshop.features.profile.presentation

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.features.profile.domain.model.ProfileData

data class ProfileState(
    val isRefreshing: Boolean = true,
    val profile: ProfileData? = null,
    val error: PocketDataError? = null,
    val showLogoutConfirmation: Boolean = false,
    val isLoggingOut: Boolean = false,
)
