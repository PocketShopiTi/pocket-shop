package com.iti.pocketshop.common.favorites.di


import com.iti.pocketshop.common.favorites.data.repository.FavoriteRepoImpl
import com.iti.pocketshop.common.favorites.domain.repository.FavoriteRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoriteModule {

    @Binds
    @Singleton
    abstract fun bindFavoriteRepo(
        favoriteRepoImpl: FavoriteRepoImpl
    ): FavoriteRepo

}
