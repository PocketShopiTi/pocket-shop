package com.iti.pocketshop.features.profile.domain.model

sealed interface ProfileSession {
    data object Guest : ProfileSession
    data class Authenticated(val user: UserEntity) : ProfileSession
}
