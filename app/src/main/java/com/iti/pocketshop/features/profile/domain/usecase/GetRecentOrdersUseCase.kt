package com.iti.pocketshop.features.profile.domain.usecase

import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.features.profile.domain.model.OrderEntity
import com.iti.pocketshop.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject


class GetRecentOrdersUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(userId: String): PocketResult<List<OrderEntity>, PocketDataError.Remote> {
        return repository.getRecentOrders(userId, 3)
    }
}