package com.iti.pocketshop.common.favorites.domain.usecase

import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import javax.inject.Inject

class ClearFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepo
) {
    suspend operator fun invoke() {
        return repository.clearLocalFavorites()
    }
}
