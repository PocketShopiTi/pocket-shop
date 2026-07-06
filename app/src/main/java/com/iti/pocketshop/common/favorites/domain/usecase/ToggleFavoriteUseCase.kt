package com.iti.pocketshop.common.favorites.domain.usecase

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import com.iti.pocketshop.network.PocketDataError
import com.iti.pocketshop.network.PocketResult
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepo
) {
    suspend operator fun invoke(product: FavoriteProduct): PocketResult<FavoriteProduct, PocketDataError> {
        return repository.toggleFavorite(product)
    }
}
