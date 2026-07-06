package com.iti.pocketshop.features.profile.domain.model

import com.iti.pocketshop.network.PocketDataError

sealed interface ProfileLoadUpdate {
    data class Cached(val profile: ProfileData) : ProfileLoadUpdate
    data class Fresh(val profile: ProfileData.Authenticated) : ProfileLoadUpdate
    data class Failed(val error: PocketDataError) : ProfileLoadUpdate
}
