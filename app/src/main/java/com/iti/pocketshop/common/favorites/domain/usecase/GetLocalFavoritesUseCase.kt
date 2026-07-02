package com.iti.pocketshop.common.favorites.domain.usecase


import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepo
) {
    operator fun invoke(): Flow<List<FavoriteProduct>> {
        return repository.getLocalFavorites()
    }
}
