package com.iti.pocketshop.features.brands.di

import com.iti.pocketshop.features.brands.data.BrandsRemoteSourceImpl
import com.iti.pocketshop.features.brands.data.BrandsRepositoryImpl
import com.iti.pocketshop.features.brands.domain.BrandsRemoteSource
import com.iti.pocketshop.features.brands.domain.BrandsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BrandsModule {

    @Binds
    abstract fun bindBrandsRemoteSource(
        impl: BrandsRemoteSourceImpl
    ): BrandsRemoteSource

    @Binds
    abstract fun bindBrandsRepository(
        impl: BrandsRepositoryImpl
    ): BrandsRepository
}
