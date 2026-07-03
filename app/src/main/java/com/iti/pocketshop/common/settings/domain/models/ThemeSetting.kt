package com.iti.pocketshop.common.settings.domain.models

import com.iti.pocketshop.R
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeSetting {
    LIGHT,
    DARK,
    FOLLOW_SYSTEM;

    fun getTitleId(): Int {
        return when (this) {
            LIGHT -> R.string.light
            DARK -> R.string.dark
            FOLLOW_SYSTEM -> R.string.follow_system
        }
    }
}
