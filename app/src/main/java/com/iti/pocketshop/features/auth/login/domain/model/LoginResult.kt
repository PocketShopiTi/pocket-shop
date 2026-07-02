package com.iti.pocketshop.features.auth.login.domain.model

sealed interface LoginOutcome {
    data object Ready : LoginOutcome
    data object NeedsEmailVerification : LoginOutcome
}
