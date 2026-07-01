package com.iti.pocketshop.common.favorites.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.iti.pocketshop.common.favorites.data.local.FavoriteDao
import com.iti.pocketshop.common.favorites.data.mapper.toDomain
import com.iti.pocketshop.common.favorites.data.mapper.toEntity
import com.iti.pocketshop.common.favorites.data.mapper.toFirebaseFavorite
import com.iti.pocketshop.common.favorites.data.remote.FirebaseFavoriteProduct
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
        return favoriteDao.getFavorites().map { entities ->
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

        val favProductEntity = product.toEntity(userId)
        if (isFav) {
            favoriteDao.deleteFavorite(favProductEntity)
        } else {
            favoriteDao.insertFavorite(favProductEntity)
        }

        val fireFavProduct = favProductEntity.toFirebaseFavorite()

        return try {
            val docRef = firestore.collection(FirestoreTables.FAVORITES)
                .document(userId)
                .collection(FirestoreTables.PRODUCTS)
                .document(fireFavProduct.id ?: return PocketResult.Error(PocketDataError.Firestore.NOT_FOUND))

            if (isFav) {
                docRef.delete().await()
            } else {
                docRef.set(fireFavProduct).await()
            }
            PocketResult.Success(product)
        } catch (e: Exception) {
            if (isFav) {
                favoriteDao.insertFavorite(favProductEntity)
            } else {
                favoriteDao.deleteFavorite(favProductEntity)
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

            val favoriteProductEntities = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirebaseFavoriteProduct::class.java)
                    ?.toEntity()
            }

            favoriteDao.clearFavorites(userId)
            favoriteDao.insertAllFavorite(favoriteProductEntities)

            val products = favoriteProductEntities.map { it.toDomain() }

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
