package com.iti.pocketshop.features.categories.di

import com.iti.pocketshop.features.categories.data.CategoriesRemoteSourceImpl
import com.iti.pocketshop.features.categories.data.CategoriesRepositoryImpl
import com.iti.pocketshop.features.categories.domain.CategoriesRemoteSource
import com.iti.pocketshop.features.categories.domain.CategoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoriesModule {

    @Binds
    abstract fun bindCategoriesRemoteSource(
        impl: CategoriesRemoteSourceImpl
    ): CategoriesRemoteSource

    @Binds
    abstract fun bindCategoriesRepository(
        impl: CategoriesRepositoryImpl
    ): CategoriesRepository
}
