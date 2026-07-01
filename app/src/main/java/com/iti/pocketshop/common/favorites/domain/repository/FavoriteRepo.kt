package com.iti.pocketshop.common.favorites.domain.repository

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import kotlinx.coroutines.flow.Flow

interface FavoriteRepo {
    fun getLocalFavorites(): Flow<List<FavoriteProduct>>

    suspend fun toggleFavorite(product: FavoriteProduct): PocketResult<FavoriteProduct, PocketDataError>

    fun isFavorite(productId: String): Flow<Boolean>

    suspend fun syncFavoritesWithRemote(): PocketResult<List<FavoriteProduct>, PocketDataError>

    suspend fun clearLocalFavorites()
}
