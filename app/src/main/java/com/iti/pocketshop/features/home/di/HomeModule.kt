package com.iti.pocketshop.features.home.di

import com.iti.pocketshop.features.home.data.HomeRemoteSourceImpl
import com.iti.pocketshop.features.home.data.HomeRepositoryImpl
import com.iti.pocketshop.features.home.domain.HomeRemoteSource
import com.iti.pocketshop.features.home.domain.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    abstract fun bindHomeRemoteSource(
        impl: HomeRemoteSourceImpl,
    ): HomeRemoteSource

    @Binds
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl,
    ): HomeRepository
}