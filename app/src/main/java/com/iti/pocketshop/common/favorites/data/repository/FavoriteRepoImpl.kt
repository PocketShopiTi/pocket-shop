package com.iti.pocketshop.common.favorites.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.iti.pocketshop.common.favorites.data.local.FavoriteDao
import com.iti.pocketshop.common.favorites.data.mapper.toDomain
import com.iti.pocketshop.common.favorites.data.mapper.toEntity
import com.iti.pocketshop.common.favorites.domain.constants.FirestoreTables
import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import com.iti.pocketshop.core.networkutils.PocketDataError
import com.iti.pocketshop.core.networkutils.PocketResult
import com.iti.pocketshop.core.networkutils.toPocketFirebaseError
import com.iti.pocketshop.core.userdata.UserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class FavoriteRepoImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val firestore: FirebaseFirestore,
    private val userRepo: UserRepo,
) : FavoriteRepo {

    override fun getLocalFavorites(): Flow<List<FavoriteProduct>> {
        val userId = userRepo.currentUser?.uid ?: return flowOf(emptyList())
        return favoriteDao.getFavorites(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(
        product: FavoriteProduct
    ): PocketResult<FavoriteProduct, PocketDataError> {

        val userId = userRepo.currentUser?.uid ?: return PocketResult.Error(
            PocketDataError.Firestore.PERMISSION_DENIED
        )

        val isFav = favoriteDao.isFavoriteOnce(product.id, userId)

        val favProduct = product.toEntity(userId)
        if (isFav) {
            favoriteDao.deleteFavorite(favProduct)
        } else {
            favoriteDao.insertFavorite(favProduct)
        }

        return try {
            val docRef = firestore.collection(FirestoreTables.FAVORITES)
                .document(userId)
                .collection(FirestoreTables.PRODUCTS)
                .document(product.id)

            if (isFav) {
                docRef.delete().await()
            } else {
                docRef.set(product).await()
            }
            PocketResult.Success(product)
        } catch (e: Exception) {
            if (isFav) {
                favoriteDao.insertFavorite(favProduct)
            } else {
                favoriteDao.deleteFavorite(favProduct)
            }
            PocketResult.Error(e.toPocketFirebaseError())
        }
    }

    override fun isFavorite(productId: String): Flow<Boolean> {
        val userId = userRepo.currentUser?.uid ?: return flowOf(false)
        return favoriteDao.isFavorite(productId, userId)
    }

    override suspend fun syncFavoritesWithRemote(): PocketResult<List<FavoriteProduct>, PocketDataError> {
        val userId = userRepo.currentUser?.uid ?: return PocketResult.Error(
            PocketDataError.Firestore.PERMISSION_DENIED
        )

        return try {
            val snapshot = firestore.collection(FirestoreTables.FAVORITES)
                .document(userId)
                .collection(FirestoreTables.PRODUCTS)
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FavoriteProduct::class.java)
            }

            val favoriteProductEntities = products.map { it.toEntity(userId) }

            favoriteDao.clearFavorites(userId)
            favoriteDao.insertAllFavorite(favoriteProductEntities)

            PocketResult.Success(products)
        } catch (e: Exception) {
            PocketResult.Error(e.toPocketFirebaseError())
        }
    }

    override suspend fun clearLocalFavorites() {
        val userId = userRepo.currentUser?.uid ?: return
        favoriteDao.clearFavorites(userId)
    }
}
