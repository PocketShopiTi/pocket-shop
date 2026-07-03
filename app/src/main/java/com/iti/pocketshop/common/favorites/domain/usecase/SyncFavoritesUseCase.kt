package com.iti.pocketshop.common.favorites.domain.usecase

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import javax.inject.Inject

class SyncFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepo
) {
    suspend operator fun invoke(): PocketResult<List<FavoriteProduct>, PocketDataError> {
        return repository.syncFavoritesWithRemote()
    }
}
