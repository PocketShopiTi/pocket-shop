package com.iti.pocketshop.core.sessionmanager.domain.usecase

import com.iti.pocketshop.core.sessionmanager.domain.model.UserSession
import com.iti.pocketshop.core.sessionmanager.domain.repository.UserRepo
import javax.inject.Inject

class GetCurrentUserSessionUseCase @Inject constructor(
    private val repository: UserRepo,
) {
    operator fun invoke(): UserSession? = repository.currentSession()
}
