package com.iti.pocketshop.common.favorites.domain.usecase

import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepo
) {
    operator fun invoke(productId: String): Flow<Boolean> {
        return repository.isFavorite(productId)
    }
}
