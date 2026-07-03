package com.iti.pocketshop.common.settings.domain.models

import com.iti.pocketshop.R
import kotlinx.serialization.Serializable

@Serializable
enum class CurrencySetting {
    EGP,
    USD;

    fun getTitleId(): Int {
        return when (this) {
            EGP -> R.string.egp
            USD -> R.string.usd
        }
    }
}